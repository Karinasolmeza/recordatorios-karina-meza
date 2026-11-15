package com.example.recordatorios_karinameza.data

import com.example.recordatorios_karinameza.Recordatorio
import com.example.recordatorios_karinameza.data.local.RecordatorioDao
import com.example.recordatorios_karinameza.data.local.toDomain
import com.example.recordatorios_karinameza.data.local.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class RoomRecordatorioRepository(
    private val dao: RecordatorioDao,
    private val uid: String
) : RecordatorioRepository {

    override suspend fun obtenerRecordatorios(): List<Recordatorio> {
        return dao.observarTodosPorUsuario(uid)
            .first()
            .map { it.toDomain() }
    }

    override fun observarRecordatorios(): Flow<List<Recordatorio>> {
        return dao.observarTodosPorUsuario(uid)
            .map { lista ->
                lista.map { it.toDomain() }
            }
    }

    override suspend fun obtenerRecordatorioPorId(
        id: Int
    ): Recordatorio? {
        return dao.obtenerPorIdYUsuario(
            id = id,
            uid = uid
        )?.toDomain()
    }

    override suspend fun guardarRecordatorio(
        recordatorio: Recordatorio
    ): Recordatorio {

        if (recordatorio.id != 0) {
            return actualizarRecordatorio(recordatorio)
        }

        val ahora = System.currentTimeMillis()

        val nuevoRecordatorio = recordatorio.copy(
            fechaCreacion = ahora,
            fechaActualizacion = ahora,
            propietarioUid = uid,
            pendienteSincronizacion = true,
            eliminadoPendienteSincronizacion = false
        )

        val nuevoId = dao.insertar(
            nuevoRecordatorio.toEntity()
        )

        return nuevoRecordatorio.copy(
            id = nuevoId.toInt()
        )
    }

    override suspend fun actualizarRecordatorio(
        recordatorio: Recordatorio
    ): Recordatorio {

        require(recordatorio.id > 0) {
            "El identificador debe ser mayor que cero"
        }

        val existente = dao.obtenerPorIdYUsuario(
            id = recordatorio.id,
            uid = uid
        ) ?: throw IllegalStateException(
            "No se encontró el recordatorio para este usuario"
        )

        val recordatorioActualizado = recordatorio.copy(
            propietarioUid = uid,
            fechaCreacion = existente.fechaCreacion,
            fechaActualizacion = System.currentTimeMillis(),
            pendienteSincronizacion = true,
            eliminadoPendienteSincronizacion = false
        )

        val filasActualizadas = dao.actualizar(
            recordatorioActualizado.toEntity()
        )

        check(filasActualizadas > 0) {
            "No se pudo actualizar el recordatorio"
        }

        return recordatorioActualizado
    }

    override suspend fun eliminarRecordatorio(
        id: Int
    ): Boolean {

        if (id <= 0) return false

        val recordatorio = dao.obtenerPorIdYUsuario(
            id = id,
            uid = uid
        ) ?: return false

        return if (recordatorio.identificadorRemoto == null) {

            dao.eliminarPorIdYUsuario(
                id = id,
                uid = uid
            ) > 0

        } else {

            dao.marcarComoEliminacionPendientePorUsuario(
                id = id,
                uid = uid,
                fechaActualizacion = System.currentTimeMillis()
            ) > 0
        }
    }

    override fun observarPorEstado(
        estado: String
    ): Flow<List<Recordatorio>> {

        return dao.observarPorEstadoYUsuario(
            estado = estado,
            uid = uid
        ).map { lista ->
            lista.map { it.toDomain() }
        }
    }
}
