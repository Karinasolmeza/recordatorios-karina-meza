package com.example.recordatorios_karinameza.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.recordatorios_karinameza.data.PreferenciasRepository
import com.example.recordatorios_karinameza.data.RecordatorioRepository

class RecordatoriosViewModelFactory(
    private val repository: RecordatorioRepository,
    private val preferenciasRepository: PreferenciasRepository,
    private val programarSincronizacion: () -> Unit
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(RecordatoriosViewModel::class.java)) {
            return RecordatoriosViewModel(
                repository = repository,
                preferenciasRepository = preferenciasRepository,
                programarSincronizacion = programarSincronizacion
            ) as T
        }

        throw IllegalArgumentException(
            "ViewModel no reconocido: ${modelClass.name}"
        )
    }
}
