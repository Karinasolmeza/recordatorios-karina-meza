package com.example.recordatorios_karinameza.viewmodel

import com.example.recordatorios_karinameza.Recordatorio
import com.example.recordatorios_karinameza.Categoria
import com.example.recordatorios_karinameza.Prioridad

sealed interface ListadoUiState {

    data object Cargando : ListadoUiState

    data object Vacio : ListadoUiState

    data class ConDatos(
        val recordatorios: List<Recordatorio>
    ) : ListadoUiState

    data class Error(
        val mensaje: String
    ) : ListadoUiState
}

data class FormularioUiState(
    val titulo: String = "",
    val descripcion: String = "",
    val categoria: Categoria? = null,
    val prioridad: Prioridad? = null,
    val errorTitulo: String? = null,
    val errorDescripcion: String? = null,
    val errorGeneral: String? = null,
    val enviando: Boolean = false,
    val guardadoExitoso: Boolean = false
) {
    val esValido: Boolean
        get() = titulo.trim().length >= 4 &&
                descripcion.length <= 200 &&
                categoria != null &&
                prioridad != null
}

sealed interface DetalleUiState {
    data object Cargando : DetalleUiState
    data class ConDatos(val recordatorio: Recordatorio) : DetalleUiState
    data object NoEncontrado : DetalleUiState
    data class Error(val mensaje: String) : DetalleUiState
}