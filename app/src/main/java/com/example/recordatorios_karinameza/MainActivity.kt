package com.example.recordatorios_karinameza

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recordatorios_karinameza.data.OfflineFirstRecordatorioRepository
import com.example.recordatorios_karinameza.data.RoomRecordatorioRepository
import com.example.recordatorios_karinameza.data.auth.AuthRepository
import com.example.recordatorios_karinameza.data.local.RecordatoriosDatabase
import com.example.recordatorios_karinameza.data.obtenerPreferenciasRepository
import com.example.recordatorios_karinameza.data.remote.FirestoreRecordatorioRepository
import com.example.recordatorios_karinameza.data.sync.RecordatorioSyncScheduler
import com.example.recordatorios_karinameza.navigation.AppNavigation
import com.example.recordatorios_karinameza.ui.screens.IniciarSesionScreen
import com.example.recordatorios_karinameza.ui.screens.RegistroScreen
import com.example.recordatorios_karinameza.ui.theme.RecordatoriosKarinaMezaTheme
import com.example.recordatorios_karinameza.viewmodel.AuthViewModel
import com.example.recordatorios_karinameza.viewmodel.RecordatoriosViewModel
import com.example.recordatorios_karinameza.viewmodel.RecordatoriosViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RecordatoriosKarinaMezaTheme {
                AplicacionConAutenticacion()
            }
        }
    }

    @Composable
    private fun AplicacionConAutenticacion() {

        val authViewModel: AuthViewModel = viewModel(
            factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    modelClass: Class<T>
                ): T {
                    if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                        return AuthViewModel(AuthRepository()) as T
                    }

                    throw IllegalArgumentException(
                        "ViewModel no reconocido: ${modelClass.name}"
                    )
                }
            }
        )

        val authState by authViewModel.uiState.collectAsState()

        var mostrarRegistro by remember {
            mutableStateOf(false)
        }

        when {
            authState.cargando -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            authState.usuario == null -> {
                if (mostrarRegistro) {
                    RegistroScreen(
                        authViewModel = authViewModel,
                        onIrAIniciarSesion = {
                            authViewModel.limpiarError()
                            mostrarRegistro = false
                        }
                    )
                } else {
                    IniciarSesionScreen(
                        authViewModel = authViewModel,
                        onIrARegistro = {
                            authViewModel.limpiarError()
                            mostrarRegistro = true
                        }
                    )
                }
            }

            else -> {
                val uid = authState.usuario!!.uid

                AplicacionAutenticada(
                    uid = uid,
                    authViewModel = authViewModel
                )
            }
        }
    }

    @Composable
    private fun AplicacionAutenticada(
        uid: String,
        authViewModel: AuthViewModel
    ) {
        val database = RecordatoriosDatabase.obtenerInstancia(
            applicationContext
        )

        val dao = database.recordatorioDao()

        val repositoryLocal = remember(uid) {
            RoomRecordatorioRepository(
                dao = dao,
                uid = uid
            )
        }

        val repositoryRemoto = remember {
            FirestoreRecordatorioRepository()
        }

        val repository = remember(uid) {
            OfflineFirstRecordatorioRepository(
                local = repositoryLocal,
                dao = dao,
                remoto = repositoryRemoto,
                uid = uid
            )
        }

        val preferenciasRepository = remember {
            applicationContext.obtenerPreferenciasRepository()
        }

        val factory = remember(uid) {
            RecordatoriosViewModelFactory(
                repository = repository,
                preferenciasRepository = preferenciasRepository,
                programarSincronizacion = {
                    RecordatorioSyncScheduler.programar(
                        applicationContext,
                        uid
                    )
                }
            )
        }

        val recordatoriosViewModel: RecordatoriosViewModel = viewModel(
            key = "recordatorios_$uid",
            factory = factory
        )

        LaunchedEffect(uid) {
            RecordatorioSyncScheduler.programar(
                applicationContext,
                uid
            )
        }

        AppNavigation(
            viewModel = recordatoriosViewModel,
            onCerrarSesion = {
                RecordatorioSyncScheduler.cancelar(
                    applicationContext,
                    uid
                )

                authViewModel.cerrarSesion()
            }
        )
    }
}
