package com.example.recordatorios_karinameza.data

import com.example.recordatorios_karinameza.Recordatorio

interface RecordatorioRepository {

    suspend fun obtenerRecordatorios(): List<Recordatorio>

    suspend fun obtenerRecordatorioPorId(id: Int): Recordatorio?

    suspend fun guardarRecordatorio(recordatorio: Recordatorio): Recordatorio
}