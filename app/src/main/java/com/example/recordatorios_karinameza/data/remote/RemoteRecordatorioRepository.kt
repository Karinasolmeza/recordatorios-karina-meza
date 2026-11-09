package com.example.recordatorios_karinameza.data.remote

import kotlinx.coroutines.CancellationException
import java.io.IOException
import java.net.SocketTimeoutException

sealed interface ResultadoRemoto<out T> {

    data class Exito<T>(
        val datos: T
    ) : ResultadoRemoto<T>

    data class ErrorHttp(
        val codigo: Int,
        val mensaje: String,
        val reintentable: Boolean = codigo >= 500 || codigo == 408 || codigo == 429
    ) : ResultadoRemoto<Nothing>

    data class ErrorConexion(
        val mensaje: String
    ) : ResultadoRemoto<Nothing>

    data class ErrorTimeout(
        val mensaje: String
    ) : ResultadoRemoto<Nothing>

    data class ErrorDesconocido(
        val mensaje: String
    ) : ResultadoRemoto<Nothing>
}

class RemoteRecordatorioRepository(
    private val api: RecordatorioApiService
) {

    suspend fun obtenerRecordatorios():
            ResultadoRemoto<List<RecordatorioDto>> {
        return ejecutarSolicitud {
            val response = api.obtenerRecordatorios()

            if (response.isSuccessful) {
                response.body().orEmpty()
            } else {
                throw ErrorRespuestaHttp(
                    response.code(),
                    response.message()
                )
            }
        }
    }

    suspend fun crearRecordatorio(
        recordatorio: RecordatorioDto
    ): ResultadoRemoto<RecordatorioDto> {
        return ejecutarSolicitud {
            val response = api.crearRecordatorio(recordatorio)

            if (response.isSuccessful) {
                response.body()
                    ?: throw IllegalStateException(
                        "El servidor no devolvió el recordatorio creado"
                    )
            } else {
                throw ErrorRespuestaHttp(
                    response.code(),
                    response.message()
                )
            }
        }
    }

    suspend fun actualizarRecordatorio(
        id: String,
        recordatorio: RecordatorioDto
    ): ResultadoRemoto<RecordatorioDto> {
        return ejecutarSolicitud {
            val response = api.actualizarRecordatorio(id, recordatorio)

            if (response.isSuccessful) {
                response.body()
                    ?: throw IllegalStateException(
                        "El servidor no devolvió el recordatorio actualizado"
                    )
            } else {
                throw ErrorRespuestaHttp(
                    response.code(),
                    response.message()
                )
            }
        }
    }

    suspend fun eliminarRecordatorio(
        id: String
    ): ResultadoRemoto<Unit> {
        return ejecutarSolicitud {
            val response = api.eliminarRecordatorio(id)

            if (response.isSuccessful) {
                Unit
            } else {
                throw ErrorRespuestaHttp(
                    response.code(),
                    response.message()
                )
            }
        }
    }

    private suspend fun <T> ejecutarSolicitud(
        solicitud: suspend () -> T
    ): ResultadoRemoto<T> {
        return try {
            ResultadoRemoto.Exito(solicitud())

        } catch (exception: CancellationException) {
            throw exception

        } catch (exception: ErrorRespuestaHttp) {
            ResultadoRemoto.ErrorHttp(
                codigo = exception.codigo,
                mensaje = exception.message ?: "Error HTTP"
            )

        } catch (exception: SocketTimeoutException) {
            ResultadoRemoto.ErrorTimeout(
                "Se agotó el tiempo de espera de la conexión"
            )

        } catch (exception: IOException) {
            ResultadoRemoto.ErrorConexion(
                "No se pudo establecer conexión con el servidor"
            )

        } catch (exception: Exception) {
            ResultadoRemoto.ErrorDesconocido(
                exception.message ?: "Ocurrió un error inesperado"
            )
        }
    }

    private class ErrorRespuestaHttp(
        val codigo: Int,
        mensaje: String
    ) : Exception(mensaje)
}
