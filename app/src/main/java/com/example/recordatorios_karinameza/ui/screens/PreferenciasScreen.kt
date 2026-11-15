package com.example.recordatorios_karinameza.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.recordatorios_karinameza.data.OrdenamientoRecordatorios
import com.example.recordatorios_karinameza.data.PreferenciasAplicacion

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun PreferenciasScreen(
    preferencias: PreferenciasAplicacion,
    onTemaChange: (Boolean) -> Unit,
    onMostrarCompletadosChange: (Boolean) -> Unit,
    onOrdenamientoChange: (OrdenamientoRecordatorios) -> Unit,
    onVolver: () -> Unit,
    onCerrarSesion: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF30343B),
                    contentColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Apariencia",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Tema oscuro")
                        Switch(
                            checked = preferencias.temaOscuro,
                            onCheckedChange = onTemaChange
                        )
                    }
                }
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF30343B),
                    contentColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Listado",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Mostrar completados")
                        Switch(
                            checked = preferencias.mostrarCompletados,
                            onCheckedChange = onMostrarCompletadosChange
                        )
                    }

                    Text("Ordenar recordatorios por:")

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = preferencias.ordenamiento ==
                                    OrdenamientoRecordatorios.FECHA,
                            onClick = {
                                onOrdenamientoChange(
                                    OrdenamientoRecordatorios.FECHA
                                )
                            }
                        )
                        Text("Orden original")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = preferencias.ordenamiento ==
                                    OrdenamientoRecordatorios.PRIORIDAD,
                            onClick = {
                                onOrdenamientoChange(
                                    OrdenamientoRecordatorios.PRIORIDAD
                                )
                            }
                        )
                        Text("Prioridad")
                    }
                }
            }

            Button(
                onClick = onVolver,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver")
            }

            Button(
                onClick = onCerrarSesion,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar sesión")
            }
        }
    }
}
