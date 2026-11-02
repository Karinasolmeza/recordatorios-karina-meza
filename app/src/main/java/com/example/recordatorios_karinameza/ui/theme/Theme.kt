package com.example.recordatorios_karinameza.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFA3FF0E),
    onPrimary = Color(0xFF172000),

    secondary = Color(0xFFA3FF0E),
    onSecondary = Color(0xFF172000),

    tertiary = Color(0xFFB9F56B),
    onTertiary = Color(0xFF172000),

    background = Color(0xFF101114),
    onBackground = Color(0xFFFFFFFF),

    surface = Color(0xFF191B20),
    onSurface = Color(0xFFFFFFFF),

    surfaceVariant = Color(0xFF292C32),
    onSurfaceVariant = Color(0xFFE0E2E7),

    outline = Color(0xFF858A91),
    error = Color(0xFFFF6B6B),
    onError = Color(0xFF000000)
)

@Composable
fun RecordatoriosKarinaMezaTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
