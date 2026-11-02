
package com.example.recordatorios_karinameza

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.recordatorios_karinameza.data.InMemoryRecordatorioRepository
import com.example.recordatorios_karinameza.data.ModoSimulacion
import com.example.recordatorios_karinameza.navigation.AppNavigation
import com.example.recordatorios_karinameza.ui.theme.RecordatoriosKarinaMezaTheme
import com.example.recordatorios_karinameza.viewmodel.RecordatoriosViewModel
import com.example.recordatorios_karinameza.viewmodel.RecordatoriosViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val modoPrueba = ModoSimulacion.NORMAL

        val repository = InMemoryRecordatorioRepository(modoPrueba)
        val factory = RecordatoriosViewModelFactory(repository)

        val viewModel = factory.create(RecordatoriosViewModel::class.java)

        setContent {
            RecordatoriosKarinaMezaTheme {
                AppNavigation(viewModel = viewModel)
            }
        }
    }
}
