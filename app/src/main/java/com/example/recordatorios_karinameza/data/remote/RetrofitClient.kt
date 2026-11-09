package com.example.recordatorios_karinameza.data.remote

import com.example.recordatorios_karinameza.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private val loggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

    private val okHttpClient: OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

    private fun crearServicioReal(): RecordatorioApiService {
        val baseUrl = BuildConfig.RECORDATORIOS_BASE_URL

        require(baseUrl.endsWith("/")) {
            "La URL base debe terminar con /"
        }

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RecordatorioApiService::class.java)
    }

    val recordatorioApi: RecordatorioApiService by lazy {
        if (BuildConfig.RECORDATORIOS_BASE_URL.isBlank()) {
            SimulatedRecordatorioApiService
        } else {
            crearServicioReal()
        }
    }
}
