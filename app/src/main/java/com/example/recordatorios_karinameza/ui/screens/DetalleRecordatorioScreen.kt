package com.example.recordatorios_karinameza.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.recordatorios_karinameza.Recordatorio

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun DetalleRecordatorioScreen(
    recordatorio: Recordatorio?,
    onVolver: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del recordatorio") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (recordatorio == null) {
                Text("No se encontró el recordatorio.")
            } else {
                Card {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = recordatorio.titulo,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text("Descripción: ${recordatorio.descripcion ?: "Sin descripción"}")
                        Text("Categoría: ${recordatorio.categoria.etiqueta}")
                        Text("Prioridad: ${recordatorio.prioridad.etiqueta}")
                        Text("Estado: ${recordatorio.estado.etiqueta}")
                        Text("ID: ${recordatorio.id}")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = onVolver) {
                Text("Volver")
            }
        }
    }
}