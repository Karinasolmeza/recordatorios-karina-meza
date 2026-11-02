package com.example.recordatorios_karinameza.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.recordatorios_karinameza.Recordatorio
import com.example.recordatorios_karinameza.ui.components.EstadoContenido
import com.example.recordatorios_karinameza.ui.components.TipoEstadoContenido
import com.example.recordatorios_karinameza.viewmodel.ListadoUiState

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun ListadoScreen(
    uiState: ListadoUiState,
    onCrearRecordatorio: () -> Unit,
    onRecordatorioClick: (Int) -> Unit,
    onReintentar: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Recordatorios Karina Meza Teclab")
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCrearRecordatorio,
                containerColor = Color(0xFFA3FF0E),
                contentColor = Color(0xFF000000)
            ) {
                Text("+")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            when (uiState) {

                is ListadoUiState.Cargando -> {
                    EstadoContenido(
                        tipo = TipoEstadoContenido.CARGANDO,
                        mensaje = "Cargando...",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is ListadoUiState.Vacio -> {
                    EstadoContenido(
                        tipo = TipoEstadoContenido.VACIO,
                        mensaje = "Todavía no hay recordatorios.",
                        textoAccion = "Crear",
                        onAccion = onCrearRecordatorio,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is ListadoUiState.Error -> {
                    EstadoContenido(
                        tipo = TipoEstadoContenido.ERROR,
                        mensaje = uiState.mensaje,
                        textoAccion = "Reintentar",
                        onAccion = onReintentar,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is ListadoUiState.ConDatos -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = uiState.recordatorios,
                            key = { it.id }
                        ) { recordatorio ->
                            RecordatorioCard(
                                recordatorio = recordatorio,
                                onClick = {
                                    onRecordatorioClick(recordatorio.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecordatorioCard(
    recordatorio: Recordatorio,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (colorChip, colorTextoChip) =
        when (recordatorio.prioridad.etiqueta.lowercase()) {
            "alta" -> Color(0xFFF28C45) to Color(0xFF38200F)
            "media" -> Color(0xFFFFD95A) to Color(0xFF382B00)
            "baja" -> Color(0xFF75B9F5) to Color(0xFF102B45)
            else -> Color(0xFFBDBDBD) to Color(0xFF202020)
        }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF30343B),
            contentColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = recordatorio.titulo,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )

            Text(
                text = "Categoría: ${recordatorio.categoria.etiqueta}",
                color = Color.White
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Prioridad:",
                    color = Color.White
                )

                Surface(
                    color = colorChip,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = recordatorio.prioridad.etiqueta,
                        color = colorTextoChip,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 5.dp
                        )
                    )
                }
            }

            Text(
                text = "Estado: ${recordatorio.estado.etiqueta}",
                color = Color.White
            )
        }
    }
}
