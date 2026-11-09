package com.example.recordatorios_karinameza.data

import com.example.recordatorios_karinameza.Recordatorio
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

interface RecordatorioRepository {

    suspend fun obtenerRecordatorios(): List<Recordatorio>

    fun observarRecordatorios(): Flow<List<Recordatorio>> =
        flow {
            emit(obtenerRecordatorios())
        }

    suspend fun obtenerRecordatorioPorId(
        id: Int
    ): Recordatorio?

    suspend fun guardarRecordatorio(
        recordatorio: Recordatorio
    ): Recordatorio

    suspend fun actualizarRecordatorio(
        recordatorio: Recordatorio
    ): Recordatorio {
        return guardarRecordatorio(recordatorio)
    }

    suspend fun eliminarRecordatorio(id: Int): Boolean {
        return false
    }

    fun observarPorEstado(
        estado: String
    ): Flow<List<Recordatorio>> {
        return observarRecordatorios().map { lista ->
            lista.filter { it.estado.etiqueta == estado }
        }
    }
}
