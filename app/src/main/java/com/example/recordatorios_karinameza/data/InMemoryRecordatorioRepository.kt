
package com.example.recordatorios_karinameza.data

import com.example.recordatorios_karinameza.Recordatorio
import com.example.recordatorios_karinameza.recordatorios
import kotlinx.coroutines.delay

enum class ModoSimulacion {
    NORMAL,
    LISTADO_VACIO,
    ERROR_CARGA,
    ERROR_GUARDADO
}

class InMemoryRecordatorioRepository(
    private val modoSimulacion: ModoSimulacion = ModoSimulacion.NORMAL
) : RecordatorioRepository {

    private val listaRecordatorios = recordatorios.toMutableList()

    override suspend fun obtenerRecordatorios(): List<Recordatorio> {
        delay(1500)

        return when (modoSimulacion) {
            ModoSimulacion.NORMAL,
            ModoSimulacion.ERROR_GUARDADO -> {
                listaRecordatorios.toList()
            }

            ModoSimulacion.LISTADO_VACIO -> {
                emptyList()
            }

            ModoSimulacion.ERROR_CARGA -> {
                throw Exception("Error simulado al cargar los recordatorios")
            }
        }
    }

    override suspend fun obtenerRecordatorioPorId(
        id: Int
    ): Recordatorio? {
        delay(1000)

        if (modoSimulacion == ModoSimulacion.ERROR_CARGA) {
            throw Exception("Error simulado al cargar el detalle")
        }

        return listaRecordatorios.find { it.id == id }
    }

    override suspend fun guardarRecordatorio(
        recordatorio: Recordatorio
    ): Recordatorio {
        delay(2000)

        if (modoSimulacion == ModoSimulacion.ERROR_GUARDADO) {
            throw Exception("Error al guardar el recordatorio")
        }

        val nuevoId = (listaRecordatorios.maxOfOrNull { it.id } ?: 0) + 1

        val nuevoRecordatorio = recordatorio.copy(id = nuevoId)

        listaRecordatorios.add(nuevoRecordatorio)

        return nuevoRecordatorio
    }
}
