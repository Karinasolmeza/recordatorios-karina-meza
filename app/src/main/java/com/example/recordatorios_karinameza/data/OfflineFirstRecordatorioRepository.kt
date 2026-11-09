package com.example.recordatorios_karinameza.data

import com.example.recordatorios_karinameza.Recordatorio
import com.example.recordatorios_karinameza.data.local.RecordatorioDao
import com.example.recordatorios_karinameza.data.local.toDomain
import com.example.recordatorios_karinameza.data.remote.RemoteRecordatorioRepository
import com.example.recordatorios_karinameza.data.remote.ResultadoRemoto
import com.example.recordatorios_karinameza.data.remote.toDto
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

data class ResultadoSincronizacion(
    val sincronizados: Int,
    val requiereReintento: Boolean,
    val erroresPermanentes: Int
)

class OfflineFirstRecordatorioRepository(
    private val local: RoomRecordatorioRepository,
    private val dao: RecordatorioDao,
    private val remoto: RemoteRecordatorioRepository
) : RecordatorioRepository {

    override suspend fun obtenerRecordatorios(): List<Recordatorio> {
        return local.obtenerRecordatorios()
    }

    override fun observarRecordatorios(): Flow<List<Recordatorio>> {
        return local.observarRecordatorios()
    }

    override suspend fun obtenerRecordatorioPorId(
        id: Int
    ): Recordatorio? {
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

    suspend fun sincronizarPendientes(): ResultadoSincronizacion {
        var sincronizados = 0
        var requiereReintento = false
        var erroresPermanentes = 0

        val pendientes = dao.observarPendientesSincronizacion().first()

        for (entity in pendientes) {
            try {
                val recordatorio = entity.toDomain()
                val dto = recordatorio.toDto()

                val resultado = if (recordatorio.identificadorRemoto == null) {
                    remoto.crearRecordatorio(dto)
                } else {
                    remoto.actualizarRecordatorio(
                        recordatorio.identificadorRemoto,
                        dto
                    )
                }

                when (resultado) {
                    is ResultadoRemoto.Exito -> {
                        val idRemoto = resultado.datos.id
                            ?: recordatorio.identificadorRemoto

                        if (idRemoto != null) {
                            val filasActualizadas =
                                dao.marcarComoSincronizado(
                                    id = recordatorio.id,
                                    identificadorRemoto = idRemoto,
                                    fechaActualizacion = recordatorio.fechaActualizacion
                                )

                            if (filasActualizadas > 0) {
                                sincronizados++
                            } else {
                                requiereReintento = true
                            }
                        } else {
                            requiereReintento = true
                        }
                    }

                    is ResultadoRemoto.ErrorHttp -> {
                        if (resultado.reintentable) {
                            requiereReintento = true
                        } else {
                            erroresPermanentes++
                        }
                    }

                    is ResultadoRemoto.ErrorConexion,
                    is ResultadoRemoto.ErrorTimeout,
                    is ResultadoRemoto.ErrorDesconocido -> {
                        requiereReintento = true
                    }
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                requiereReintento = true
            }
        }

        val eliminacionesPendientes =
            dao.observarEliminacionesPendientes().first()

        for (entity in eliminacionesPendientes) {
            try {
                val identificadorRemoto =
                    entity.identificadorRemoto ?: continue

                val resultado =
                    remoto.eliminarRecordatorio(identificadorRemoto)

                when (resultado) {
                    is ResultadoRemoto.Exito -> {
                        val filasEliminadas = dao.eliminarPorId(entity.id)

                        if (filasEliminadas > 0) {
                            sincronizados++
                        } else {
                            requiereReintento = true
                        }
                    }

                    is ResultadoRemoto.ErrorHttp -> {
                        if (resultado.reintentable) {
                            requiereReintento = true
                        } else {
                            erroresPermanentes++
                        }
                    }

                    is ResultadoRemoto.ErrorConexion,
                    is ResultadoRemoto.ErrorTimeout,
                    is ResultadoRemoto.ErrorDesconocido -> {
                        requiereReintento = true
                    }
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
