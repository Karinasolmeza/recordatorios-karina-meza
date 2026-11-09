package com.example.recordatorios_karinameza

import java.util.UUID

data class Recordatorio(
    val id: Int,
    val titulo: String,
    val descripcion: String? = null,
    val categoria: Categoria,
    val prioridad: Prioridad,
    val estado: Estado,
    val fechaCreacion: Long = System.currentTimeMillis(),
    val fechaActualizacion: Long = System.currentTimeMillis(),
    val identificadorRemoto: String? = null,
    val pendienteSincronizacion: Boolean = true,
    val identificadorCliente: String = UUID.randomUUID().toString(),
    val eliminadoPendienteSincronizacion: Boolean = false
)
