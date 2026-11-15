package com.example.recordatorios_karinameza.data.local

import com.example.recordatorios_karinameza.Categoria
import com.example.recordatorios_karinameza.Estado
import com.example.recordatorios_karinameza.Prioridad
import com.example.recordatorios_karinameza.Recordatorio

fun RecordatorioEntity.toDomain(): Recordatorio {
    return Recordatorio(
        id = id,
        titulo = titulo,
        descripcion = descripcion,
        categoria = when (categoria) {
            "Estudio" -> Categoria.Estudio
            "Académica" -> Categoria.Academica
            "Personal" -> Categoria.Personal
            else -> Categoria.Personal
        },
        prioridad = when (prioridad) {
            "Alta" -> Prioridad.Alta
            "Media" -> Prioridad.Media
            "Baja" -> Prioridad.Baja
            else -> Prioridad.Media
        },
        estado = when (estado) {
            "Pendiente" -> Estado.Pendiente
            "Completado" -> Estado.Completado
            else -> Estado.Pendiente
        },
        fechaCreacion = fechaCreacion,
        fechaActualizacion = fechaActualizacion,
        identificadorCliente = identificadorCliente,
        identificadorRemoto = identificadorRemoto,
        pendienteSincronizacion = pendienteSincronizacion,
        eliminadoPendienteSincronizacion = eliminadoPendienteSincronizacion,
        propietarioUid = propietarioUid
    )
}

fun Recordatorio.toEntity(): RecordatorioEntity {
    return RecordatorioEntity(
        id = id,
        titulo = titulo,
        descripcion = descripcion,
        categoria = categoria.etiqueta,
        prioridad = prioridad.etiqueta,
        estado = estado.etiqueta,
        fechaCreacion = fechaCreacion,
        fechaActualizacion = fechaActualizacion,
        identificadorCliente = identificadorCliente,
        identificadorRemoto = identificadorRemoto,
        pendienteSincronizacion = pendienteSincronizacion,
        eliminadoPendienteSincronizacion = eliminadoPendienteSincronizacion,
        propietarioUid = propietarioUid
    )
}
