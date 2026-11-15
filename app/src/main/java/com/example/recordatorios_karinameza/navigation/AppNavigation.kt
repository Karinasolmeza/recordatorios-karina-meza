package com.example.recordatorios_karinameza.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.recordatorios_karinameza.ui.screens.CrearRecordatorioScreen
import com.example.recordatorios_karinameza.ui.screens.DetalleRecordatorioScreen
import com.example.recordatorios_karinameza.ui.screens.ListadoScreen
import com.example.recordatorios_karinameza.ui.screens.PreferenciasScreen
import com.example.recordatorios_karinameza.viewmodel.CrudUiState
import com.example.recordatorios_karinameza.viewmodel.DetalleUiState
import com.example.recordatorios_karinameza.viewmodel.RecordatoriosViewModel

sealed class AppRoute(val route: String) {
    data object Listado : AppRoute("listado")
    data object Crear : AppRoute("crear")
    data object Configuracion : AppRoute("configuracion")
    data object Detalle : AppRoute("detalle/{recordatorioId}") {
        fun crearRuta(id: Int): String {
            return "detalle/$id"
        }
    }
}

@Composable
fun AppNavigation(
    viewModel: RecordatoriosViewModel,
    onCerrarSesion: () -> Unit
) {
    val navController = rememberNavController()

    val listadoUiState by
    viewModel.listadoUiState.collectAsStateWithLifecycle()

    val formularioUiState by
    viewModel.formularioUiState.collectAsStateWithLifecycle()

    val detalleUiState by
    viewModel.detalleUiState.collectAsStateWithLifecycle()

    val preferencias by
    viewModel.preferenciasUiState.collectAsStateWithLifecycle()

    val crudUiState by
    viewModel.crudUiState.collectAsStateWithLifecycle()

    LaunchedEffect(formularioUiState.guardadoExitoso) {
        if (formularioUiState.guardadoExitoso) {
            viewModel.limpiarFormulario()
            navController.popBackStack()
        }
    }

    LaunchedEffect(crudUiState) {
        if (crudUiState == CrudUiState.Exito) {
            navController.popBackStack()
            viewModel.limpiarCrudUiState()
        }
    }

    NavHost(
        navController = navController,
        startDestination = AppRoute.Listado.route
    ) {

        composable(AppRoute.Listado.route) {
            ListadoScreen(
                uiState = listadoUiState,
                onCrearRecordatorio = {
                    viewModel.limpiarFormulario()
                    navController.navigate(AppRoute.Crear.route)
                },
                onRecordatorioClick = { id ->
                    viewModel.limpiarCrudUiState()
                    navController.navigate(
                        AppRoute.Detalle.crearRuta(id)
                    )
                },
                onReintentar = {
                    viewModel.cargarRecordatorios()
                },
                onConfiguracion = {
                    navController.navigate(
                        AppRoute.Configuracion.route
                    )
                }
            )
        }

        composable(AppRoute.Configuracion.route) {
            PreferenciasScreen(
                preferencias = preferencias,
                onTemaChange = viewModel::actualizarTema,
                onMostrarCompletadosChange =
                    viewModel::actualizarMostrarCompletados,
                onOrdenamientoChange =
                    viewModel::actualizarOrdenamiento,
                onVolver = {
                    navController.popBackStack()
                },
                onCerrarSesion = onCerrarSesion
            )
        }

        composable(AppRoute.Crear.route) {
            val editando = viewModel.estaEditando()

            CrearRecordatorioScreen(
                uiState = formularioUiState,
                onTituloChange = viewModel::actualizarTitulo,
                onDescripcionChange = viewModel::actualizarDescripcion,
                onCategoriaChange = viewModel::actualizarCategoria,
                onPrioridadChange = viewModel::actualizarPrioridad,
                onGuardar = viewModel::guardarRecordatorio,
                onVolver = {
                    viewModel.limpiarFormulario()
                    navController.popBackStack()
                },
                modoEdicion = editando
            )
        }

        composable(
            route = AppRoute.Detalle.route,
            arguments = listOf(
                navArgument("recordatorioId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val recordatorioId =
                backStackEntry.arguments?.getInt("recordatorioId") ?: 0

            LaunchedEffect(recordatorioId) {
                viewModel.cargarDetalle(recordatorioId)
            }

            when (val estado = detalleUiState) {

                is DetalleUiState.Cargando -> {
                    Scaffold { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                is DetalleUiState.ConDatos -> {
                    DetalleRecordatorioScreen(
                        recordatorio = estado.recordatorio,
                        onVolver = {
                            navController.popBackStack()
                        },
                        onEditar = { recordatorio ->
                            viewModel.prepararEdicion(recordatorio)
                            navController.navigate(AppRoute.Crear.route)
                        },
                        onEliminar = { id ->
                            viewModel.eliminarRecordatorio(id)
                        }
                    )
                }

                is DetalleUiState.NoEncontrado -> {
                    DetalleRecordatorioScreen(
                        recordatorio = null,
                        onVolver = {
                            navController.popBackStack()
                        },
                        onEditar = {},
                        onEliminar = {}
                    )
                }

                is DetalleUiState.Error -> {
                    Scaffold { innerPadding ->
                        Column(
                            modifier = Modifier
                                .padding(innerPadding)
                                .padding(16.dp)
                        ) {
                            Text(estado.mensaje)

                            Button(
                                onClick = {
                                    viewModel.cargarDetalle(recordatorioId)
                                }
                            ) {
                                Text("Reintentar")
                            }

                            Button(
                                onClick = {
                                    navController.popBackStack()
                                }
                            ) {
                                Text("Volver")
                            }
                        }
                    }
                }
            }
        }
    }
}
