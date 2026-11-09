package com.example.recordatorios_karinameza

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.recordatorios_karinameza.data.OfflineFirstRecordatorioRepository
import com.example.recordatorios_karinameza.data.RoomRecordatorioRepository
import com.example.recordatorios_karinameza.data.local.RecordatoriosDatabase
import com.example.recordatorios_karinameza.data.obtenerPreferenciasRepository
import com.example.recordatorios_karinameza.data.remote.RemoteRecordatorioRepository
import com.example.recordatorios_karinameza.data.remote.RetrofitClient
import com.example.recordatorios_karinameza.data.sync.RecordatorioSyncScheduler
import com.example.recordatorios_karinameza.navigation.AppNavigation
import com.example.recordatorios_karinameza.ui.theme.RecordatoriosKarinaMezaTheme
import com.example.recordatorios_karinameza.viewmodel.RecordatoriosViewModel
import com.example.recordatorios_karinameza.viewmodel.RecordatoriosViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = RecordatoriosDatabase.obtenerInstancia(
            applicationContext
        )

        val dao = database.recordatorioDao()

        val repositoryLocal = RoomRecordatorioRepository(dao)

        val repositoryRemoto = RemoteRecordatorioRepository(
            RetrofitClient.recordatorioApi
        )

        val repository = OfflineFirstRecordatorioRepository(
            local = repositoryLocal,
            dao = dao,
            remoto = repositoryRemoto
        )

        val preferenciasRepository =
            applicationContext.obtenerPreferenciasRepository()

        val factory = RecordatoriosViewModelFactory(
            repository,
            preferenciasRepository,
            programarSincronizacion = {
                RecordatorioSyncScheduler.programar(applicationContext)
            }
        )

        val viewModel = factory.create(
            RecordatoriosViewModel::class.java
        )

        RecordatorioSyncScheduler.programar(applicationContext)

        setContent {
            RecordatoriosKarinaMezaTheme {
                AppNavigation(viewModel = viewModel)
            }
        }
    }
}
