package com.example.recordatorios_karinameza.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "recordatorios",
    indices = [
        Index(
            value = ["identificadorCliente"],
            unique = true
        ),
        Index(
            value = ["propietarioUid", "eliminadoPendienteSincronizacion"]
        )
    ]
)
data class RecordatorioEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val titulo: String,

    val descripcion: String?,

    val categoria: String,

    val prioridad: String,

    val estado: String,

    val fechaCreacion: Long = System.currentTimeMillis(),

    val fechaActualizacion: Long = System.currentTimeMillis(),

    val identificadorCliente: String = UUID.randomUUID().toString(),

    val identificadorRemoto: String? = null,

    val pendienteSincronizacion: Boolean = true,

    val eliminadoPendienteSincronizacion: Boolean = false,

    val propietarioUid: String? = null
)
