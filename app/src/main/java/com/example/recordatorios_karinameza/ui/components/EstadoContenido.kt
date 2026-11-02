package com.example.recordatorios_karinameza.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

enum class TipoEstadoContenido {
    CARGANDO,
    VACIO,
    ERROR
}

@Composable
fun EstadoContenido(
    tipo: TipoEstadoContenido,
    mensaje: String,
    textoAccion: String? = null,
    onAccion: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        when (tipo) {
            TipoEstadoContenido.CARGANDO -> {
                CircularProgressIndicator(
                    modifier = Modifier.semantics {
                        contentDescription = "Cargando"
                    }
                )
            }

            TipoEstadoContenido.VACIO -> {
                Text(
                    text = "Sin registros",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            TipoEstadoContenido.ERROR -> {
                Text(
                    text = "Ocurrió un error",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        Text(
            text = mensaje,
            style = MaterialTheme.typography.bodyMedium
        )

        if (textoAccion != null && onAccion != null) {
            Button(
                onClick = onAccion
            ) {
                Text(textoAccion)
            }
        }
    }
}
