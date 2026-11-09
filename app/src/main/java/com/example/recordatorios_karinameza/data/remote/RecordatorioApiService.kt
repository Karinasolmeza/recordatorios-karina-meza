package com.example.recordatorios_karinameza.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RecordatorioApiService {

    @GET("recordatorios")
    suspend fun obtenerRecordatorios(): Response<List<RecordatorioDto>>

    @GET("recordatorios/{id}")
    suspend fun obtenerRecordatorioPorId(
        @Path("id") id: String
    ): Response<RecordatorioDto>

    @POST("recordatorios")
    suspend fun crearRecordatorio(
        @Body recordatorio: RecordatorioDto
    ): Response<RecordatorioDto>

    @PUT("recordatorios/{id}")
    suspend fun actualizarRecordatorio(
        @Path("id") id: String,
        @Body recordatorio: RecordatorioDto
    ): Response<RecordatorioDto>

    @DELETE("recordatorios/{id}")
    suspend fun eliminarRecordatorio(
        @Path("id") id: String
    ): Response<Unit>
}
