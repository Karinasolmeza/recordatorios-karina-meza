package com.example.recordatorios_karinameza.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.recordatorios_karinameza.Categoria
import com.example.recordatorios_karinameza.Prioridad
import com.example.recordatorios_karinameza.viewmodel.FormularioUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearRecordatorioScreen(
    uiState: FormularioUiState,
    onTituloChange: (String) -> Unit,
    onDescripcionChange: (String) -> Unit,
    onCategoriaChange: (Categoria) -> Unit,
    onPrioridadChange: (Prioridad) -> Unit,
    onGuardar: () -> Unit,
    onVolver: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Crear recordatorio")
                },
                navigationIcon = {
                    TextButton(onClick = onVolver) {
                        Text("Volver")
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            OutlinedTextField(
                value = uiState.titulo,
                onValueChange = onTituloChange,
                label = {
                    Text("Título")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentDescription = "Campo obligatorio: título"
                    },
                isError = uiState.errorTitulo != null,
                supportingText = {
                    if (uiState.errorTitulo != null) {
                        Text(
                            text = uiState.errorTitulo,
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        Text("Mínimo 4 caracteres")
                    }
                },
                singleLine = true
            )

            OutlinedTextField(
                value = uiState.descripcion,
                onValueChange = onDescripcionChange,
                label = {
                    Text("Descripción (opcional)")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentDescription = "Descripción del recordatorio"
                    },
                isError = uiState.errorDescripcion != null,
                supportingText = {
                    if (uiState.errorDescripcion != null) {
                        Text(
                            text = uiState.errorDescripcion,
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        Text("${uiState.descripcion.length}/200 caracteres")
                    }
                },
                minLines = 3
            )

            Text(
                text = "Categoría (obligatoria)",
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = uiState.categoria == Categoria.Estudio,
                    onClick = {
                        onCategoriaChange(Categoria.Estudio)
                    },
                    label = {
                        Text("Estudio")
                    }
                )

                FilterChip(
                    selected = uiState.categoria == Categoria.Academica,
                    onClick = {
                        onCategoriaChange(Categoria.Academica)
                    },
                    label = {
                        Text("Académica")
                    }
                )

                FilterChip(
                    selected = uiState.categoria == Categoria.Personal,
                    onClick = {
                        onCategoriaChange(Categoria.Personal)
                    },
                    label = {
                        Text("Personal")
                    }
                )
            }

            Text(
                text = "Prioridad (obligatoria)",
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = uiState.prioridad == Prioridad.Alta,
                    onClick = {
                        onPrioridadChange(Prioridad.Alta)
                    },
                    label = {
                        Text("Alta")
                    }
                )

                FilterChip(
                    selected = uiState.prioridad == Prioridad.Media,
                    onClick = {
                        onPrioridadChange(Prioridad.Media)
                    },
                    label = {
                        Text("Media")
                    }
                )

                FilterChip(
                    selected = uiState.prioridad == Prioridad.Baja,
                    onClick = {
                        onPrioridadChange(Prioridad.Baja)
                    },
                    label = {
                        Text("Baja")
                    }
                )
            }

            if (uiState.errorGeneral != null) {
                Text(
                    text = uiState.errorGeneral,
                    color = MaterialTheme.colorScheme.error
                )
            }

            if (uiState.guardadoExitoso) {
                Text(
                    text = "¡Recordatorio guardado correctamente!",
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Button(
                onClick = onGuardar,
                enabled = uiState.esValido && !uiState.enviando,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (uiState.enviando) {
                        "Guardando..."
                    } else {
                        "Guardar"
                    }
                )
            }

            TextButton(
                onClick = onVolver,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar")
            }
        }
    }
}
