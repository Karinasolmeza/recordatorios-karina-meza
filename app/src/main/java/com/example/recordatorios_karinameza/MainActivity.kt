
package com.example.recordatorios_karinameza

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.CardDefaults
import com.example.recordatorios_karinameza.ui.theme.RecordatoriosKarinaMezaTheme

fun colorDeRecordatorio(recordatorio: Recordatorio): Color {
    return when {
        recordatorio.estado == Estado.Completado ->
            Color(0xFFDDF3E4)

        recordatorio.prioridad == Prioridad.Alta ->
            Color(0xFFFCE0D5)

        recordatorio.prioridad == Prioridad.Media ->
            Color(0xFFFFF3B8)

        else ->
            Color.White
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            RecordatoriosKarinaMezaTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                ) { innerPadding ->
                    PantallaRecordatorios(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun PantallaRecordatorios(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Mis recordatorios Teclab Karina Meza",
            style = MaterialTheme.typography.headlineMedium
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(recordatorios) { recordatorio ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = colorDeRecordatorio(recordatorio)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = recordatorio.titulo,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(text = "Categoría: ${etiquetasCategorias[recordatorio.categoria]}")
                        Text(text = "Prioridad: ${recordatorio.prioridad.etiqueta}")
                        Text(text = "Estado: ${recordatorio.estado.etiqueta}")
                    }
                }
            }
        }
    }
}
