package com.example.recordatorios_karinameza.data.remote

import com.google.gson.annotations.SerializedName

data class RecordatorioDto(

    @SerializedName("id")
    val id: String? = null,

    @SerializedName("identificadorCliente")
    val identificadorCliente: String,

    @SerializedName("titulo")
    val titulo: String,

    @SerializedName("descripcion")
    val descripcion: String?,

    @SerializedName("categoria")
    val categoria: String,

    @SerializedName("prioridad")
    val prioridad: String,

    @SerializedName("estado")
    val estado: String,

    @SerializedName("fechaCreacion")
    val fechaCreacion: Long,

    @SerializedName("fechaActualizacion")
    val fechaActualizacion: Long
)
