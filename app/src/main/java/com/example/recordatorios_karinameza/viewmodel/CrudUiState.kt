package com.example.recordatorios_karinameza.viewmodel

sealed interface CrudUiState {
    data object Inicial : CrudUiState
    data object Procesando : CrudUiState
    data object Exito : CrudUiState
    data class Error(val mensaje: String) : CrudUiState
}
