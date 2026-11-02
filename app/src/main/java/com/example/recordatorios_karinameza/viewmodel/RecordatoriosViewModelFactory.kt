package com.example.recordatorios_karinameza.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.recordatorios_karinameza.data.RecordatorioRepository

class RecordatoriosViewModelFactory(
    private val repository: RecordatorioRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(RecordatoriosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecordatoriosViewModel(repository) as T
        }

        throw IllegalArgumentException(
            "ViewModel desconocido: ${modelClass.name}"
        )
    }
}