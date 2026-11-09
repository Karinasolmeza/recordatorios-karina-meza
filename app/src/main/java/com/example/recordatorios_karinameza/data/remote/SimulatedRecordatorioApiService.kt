package com.example.recordatorios_karinameza.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import okhttp3.ResponseBody

enum class ModoSimulacionRemota {
    NORMAL,
    ERROR_CLIENTE,
    ERROR_SERVIDOR,
    SIN_CONEXION,
    TIMEOUT
}

object SimulacionRemota {
    @Volatile
    var modo: ModoSimulacionRemota = ModoSimulacionRemota.NORMAL
}

object SimulatedRecordatorioApiService : RecordatorioApiService {

    private val registros = ConcurrentHashMap<String, RecordatorioDto>()
    private val siguienteId = AtomicLong(1)
    private val tipoContenido = "application/json".toMediaType()

    private fun <T> respuestaSegunModo(): Response<T>? {
        return when (SimulacionRemota.modo) {
            ModoSimulacionRemota.NORMAL -> null

            ModoSimulacionRemota.ERROR_CLIENTE -> Response.error(
                400,
                "Solicitud inválida".toResponseBody(tipoContenido)
            )

            ModoSimulacionRemota.ERROR_SERVIDOR -> Response.error(
                503,
                "Servicio temporalmente no disponible".toResponseBody(
                    tipoContenido
                )
            )

            ModoSimulacionRemota.SIN_CONEXION -> {
                throw IOException("No hay conexión con el servicio")
            }

            ModoSimulacionRemota.TIMEOUT -> {
                throw SocketTimeoutException("Tiempo de espera agotado")
            }
        }
    }

    private fun respuestaNoEncontrado(): ResponseBody {
        return "Recordatorio no encontrado".toResponseBody(tipoContenido)
    }

    override suspend fun obtenerRecordatorios(): Response<List<RecordatorioDto>> =
        withContext(Dispatchers.IO) {
            respuestaSegunModo<List<RecordatorioDto>>()?.let {
                return@withContext it
            }

            Response.success(
                registros.values.sortedByDescending {
                    it.fechaCreacion
                }
            )
        }

    override suspend fun obtenerRecordatorioPorId(
        id: String
    ): Response<RecordatorioDto> = withContext(Dispatchers.IO) {
        respuestaSegunModo<RecordatorioDto>()?.let {
            return@withContext it
        }

        val recordatorio = registros[id]
            ?: return@withContext Response.error(
                404,
                respuestaNoEncontrado()
            )

        Response.success(recordatorio)
    }

    override suspend fun crearRecordatorio(
        recordatorio: RecordatorioDto
    ): Response<RecordatorioDto> = withContext(Dispatchers.IO) {
        respuestaSegunModo<RecordatorioDto>()?.let {
            return@withContext it
        }

        if (
            recordatorio.titulo.isBlank() ||
            recordatorio.titulo.length < 4
        ) {
            return@withContext Response.error(
                400,
                "El título no es válido".toResponseBody(tipoContenido)
            )
        }

        val existente = registros.values.firstOrNull {
            it.identificadorCliente == recordatorio.identificadorCliente
        }

        if (existente != null) {
            return@withContext Response.success(existente)
        }

        val id = siguienteId.getAndIncrement().toString()
        val creado = recordatorio.copy(id = id)

        registros[id] = creado

        Response.success(creado)
    }

    override suspend fun actualizarRecordatorio(
        id: String,
        recordatorio: RecordatorioDto
    ): Response<RecordatorioDto> = withContext(Dispatchers.IO) {
        respuestaSegunModo<RecordatorioDto>()?.let {
            return@withContext it
        }

        if (
            recordatorio.titulo.isBlank() ||
            recordatorio.titulo.length < 4
        ) {
            return@withContext Response.error(
                400,
                "El título no es válido".toResponseBody(tipoContenido)
            )
        }

        if (!registros.containsKey(id)) {
            return@withContext Response.error(
                404,
                respuestaNoEncontrado()
            )
        }

        val actualizado = recordatorio.copy(id = id)
        registros[id] = actualizado

        Response.success(actualizado)
    }

    override suspend fun eliminarRecordatorio(
        id: String
    ): Response<Unit> = withContext(Dispatchers.IO) {
        respuestaSegunModo<Unit>()?.let {
            return@withContext it
        }

        if (registros.remove(id) == null) {
            return@withContext Response.error(
                404,
                respuestaNoEncontrado()
            )
        }

        Response.success(Unit)
    }
}
