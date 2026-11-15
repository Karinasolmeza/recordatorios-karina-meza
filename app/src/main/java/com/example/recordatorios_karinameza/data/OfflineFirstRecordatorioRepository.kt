package com.example.recordatorios_karinameza.data

import com.example.recordatorios_karinameza.Recordatorio
import com.example.recordatorios_karinameza.data.local.RecordatorioDao
import com.example.recordatorios_karinameza.data.local.toDomain
import com.example.recordatorios_karinameza.data.local.toEntity
import com.example.recordatorios_karinameza.data.remote.FirestoreRecordatorioRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

data class ResultadoSincronizacion(
    val sincronizados: Int,
    val requiereReintento: Boolean,
    val erroresPermanentes: Int
)

class OfflineFirstRecordatorioRepository(
    private val local: RoomRecordatorioRepository,
    private val dao: RecordatorioDao,
    private val remoto: FirestoreRecordatorioRepository,
    private val uid: String
) : RecordatorioRepository {

    override suspend fun obtenerRecordatorios(): List<Recordatorio> {
        return local.obtenerRecordatorios()
    }

    override fun observarRecordatorios(): Flow<List<Recordatorio>> {
        return local.observarRecordatorios()
    }

    override suspend fun obtenerRecordatorioPorId(id: Int): Recordatorio? {
        return local.obtenerRecordatorioPorId(id)
    }

    override suspend fun guardarRecordatorio(
        recordatorio: Recordatorio
    ): Recordatorio {
        return local.guardarRecordatorio(recordatorio)
    }

    override suspend fun actualizarRecordatorio(
        recordatorio: Recordatorio
    ): Recordatorio {
        return local.actualizarRecordatorio(recordatorio)
    }

    override suspend fun eliminarRecordatorio(id: Int): Boolean {
        return local.eliminarRecordatorio(id)
    }

    override fun observarPorEstado(
        estado: String
    ): Flow<List<Recordatorio>> {
        return local.observarPorEstado(estado)
    }

    suspend fun descargarDesdeFirestore(): Int {
        require(uid.isNotBlank()) {
            "El usuario no es válido."
        }

        val recordatoriosRemotos = remoto.obtenerRecordatorios(uid)

        val identificadoresRemotos = recordatoriosRemotos
            .map { it.identificadorCliente }
            .toSet()

        var incorporados = 0

        for (recordatorioRemoto in recordatoriosRemotos) {

            val existente = dao.obtenerPorIdentificadorClienteYUsuario(
                recordatorioRemoto.identificadorCliente,
                uid
            )

            if (existente != null && (
                        existente.pendienteSincronizacion ||
                                existente.eliminadoPendienteSincronizacion
                        )
            ) {
                continue
            }

            val entidad = recordatorioRemoto.copy(
                id = existente?.id ?: 0,
                propietarioUid = uid,
                pendienteSincronizacion = false,
                eliminadoPendienteSincronizacion = false
            ).toEntity()

            if (existente == null) {
                dao.insertar(entidad)
            } else {
                dao.actualizar(entidad)
            }

            incorporados++
        }

        val registrosLocales = dao.obtenerTodosPorUsuario(uid)

        for (registroLocal in registrosLocales) {

            val existeEnFirestore =
                registroLocal.identificadorCliente in identificadoresRemotos

            if (
                !existeEnFirestore &&
                registroLocal.identificadorRemoto != null &&
                !registroLocal.pendienteSincronizacion &&
                !registroLocal.eliminadoPendienteSincronizacion
            ) {
                dao.eliminarObsoletoPorUsuario(
                    id = registroLocal.id,
                    uid = uid
                )
            }
        }

        return incorporados
    }


    /**
     * Envía a Firestore los registros locales pendientes
     * y procesa las eliminaciones pendientes.
     */
    suspend fun sincronizarPendientes(): ResultadoSincronizacion {
        require(uid.isNotBlank()) { "El usuario no es válido." }

        var sincronizados = 0
        var requiereReintento = false
        var erroresPermanentes = 0

        val pendientes = dao.observarPendientesPorUsuario(uid).first()

        for (entity in pendientes) {
            try {
                val recordatorio = entity.toDomain()

                remoto.guardarRecordatorio(
                    uid = uid,
                    recordatorio = recordatorio
                )

                val filasActualizadas = dao.marcarComoSincronizadoPorUsuario(
                    id = recordatorio.id,
                    uid = uid,
                    identificadorRemoto = recordatorio.identificadorCliente,
                    fechaActualizacion = recordatorio.fechaActualizacion
                )

                if (filasActualizadas > 0) {
                    sincronizados++
                } else {
                    requiereReintento = true
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                requiereReintento = true
            }
        }

        val eliminacionesPendientes =
            dao.observarEliminacionesPendientesPorUsuario(uid).first()

        for (entity in eliminacionesPendientes) {
            try {
                // Si nunca llegó a sincronizarse, no existe en Firestore.
                if (entity.identificadorRemoto != null) {
                    remoto.eliminarRecordatorio(
                        uid = uid,
                        identificadorCliente = entity.identificadorCliente
                    )
                }

                val filasEliminadas = dao.eliminarPorIdYUsuario(
                    id = entity.id,
                    uid = uid
                )

                if (filasEliminadas > 0) {
                    sincronizados++
                } else {
                    requiereReintento = true
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                requiereReintento = true
            }
        }

        return ResultadoSincronizacion(
            sincronizados = sincronizados,
            requiereReintento = requiereReintento,
            erroresPermanentes = erroresPermanentes
        )
    }
}
