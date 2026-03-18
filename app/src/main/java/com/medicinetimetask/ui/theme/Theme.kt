package com.medicinetimetask.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF6F8F7B),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFF7B9AA6),
    tertiary = Color(0xFFE8A87C),
    background = Color(0xFFF6F3EE),
    surface = Color(0xFFFFFBF7),
    onSurface = Color(0xFF22302A),
    onSurfaceVariant = Color(0xFF66756E),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF93B49F),
    secondary = Color(0xFFA7C1CC),
    tertiary = Color(0xFFF2BC9A),
    background = Color(0xFF17201C),
    surface = Color(0xFF1E2925),
)

@Composable
fun MedicineTimeTaskTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        content = content,
    )
}
