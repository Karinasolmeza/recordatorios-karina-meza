package com.example.recordatorios_karinameza.data

import com.example.recordatorios_karinameza.Recordatorio
import com.example.recordatorios_karinameza.data.local.RecordatorioDao
import com.example.recordatorios_karinameza.data.local.toDomain
import com.example.recordatorios_karinameza.data.local.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class RoomRecordatorioRepository(
    private val dao: RecordatorioDao
) : RecordatorioRepository {

    override suspend fun obtenerRecordatorios(): List<Recordatorio> {
        return dao.observarTodos()
            .first()
            .map { it.toDomain() }
    }

    override fun observarRecordatorios(): Flow<List<Recordatorio>> {
        return dao.observarTodos().map { lista ->
            lista.map { it.toDomain() }
        }
    }

    override suspend fun obtenerRecordatorioPorId(
        id: Int
    ): Recordatorio? {
        return dao.obtenerPorId(id)?.toDomain()
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
            pendienteSincronizacion = true,
            eliminadoPendienteSincronizacion = false
        )

        val nuevoId = dao.insertar(nuevoRecordatorio.toEntity())

        return nuevoRecordatorio.copy(id = nuevoId.toInt())
    }

    override suspend fun actualizarRecordatorio(
        recordatorio: Recordatorio
    ): Recordatorio {
        require(recordatorio.id > 0) {
            "El identificador debe ser mayor que cero"
        }

        val recordatorioActualizado = recordatorio.copy(
            fechaActualizacion = System.currentTimeMillis(),
            pendienteSincronizacion = true,
            eliminadoPendienteSincronizacion = false
        )

        val filasActualizadas = dao.actualizar(
            recordatorioActualizado.toEntity()
        )

        check(filasActualizadas > 0) {
            "No se encontró el recordatorio para actualizar"
        }

        return recordatorioActualizado
    }

    override suspend fun eliminarRecordatorio(id: Int): Boolean {
        if (id <= 0) return false

        val recordatorio = dao.obtenerPorId(id) ?: return false

        return if (recordatorio.identificadorRemoto == null) {
            dao.eliminarPorId(id) > 0
        } else {
            dao.marcarComoEliminacionPendiente(
                id = id,
                fechaActualizacion = System.currentTimeMillis()
            ) > 0
        }
    }

    override fun observarPorEstado(
        estado: String
    ): Flow<List<Recordatorio>> {
        return dao.observarPorEstado(estado).map { lista ->
            lista.map { it.toDomain() }
        }
    }
}
