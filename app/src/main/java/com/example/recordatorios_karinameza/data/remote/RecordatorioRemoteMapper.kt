package com.example.recordatorios_karinameza.data.remote

import com.example.recordatorios_karinameza.Categoria
import com.example.recordatorios_karinameza.Estado
import com.example.recordatorios_karinameza.Prioridad
import com.example.recordatorios_karinameza.Recordatorio

fun RecordatorioDto.toDomain(
    idLocal: Int = 0
): Recordatorio {
    return Recordatorio(
        id = idLocal,
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
        identificadorRemoto = id,
        pendienteSincronizacion = false,
        identificadorCliente = identificadorCliente
    )
}

fun Recordatorio.toDto(): RecordatorioDto {
    return RecordatorioDto(
        id = identificadorRemoto,
        identificadorCliente = identificadorCliente,
        titulo = titulo,
        descripcion = descripcion,
        categoria = categoria.etiqueta,
        prioridad = prioridad.etiqueta,
        estado = estado.etiqueta,
        fechaCreacion = fechaCreacion,
        fechaActualizacion = fechaActualizacion
    )
}
