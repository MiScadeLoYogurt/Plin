package com.plin.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Teal = Color(0xFF0F766E)
private val Background = Color(0xFFF2EFE8)
private val OnBackground = Color(0xFF1C1917)

private val LightColors = lightColorScheme(
    primary = Teal,
    onPrimary = Color.White,
    background = Background,
    onBackground = OnBackground,
    surface = Color.White,
    onSurface = OnBackground,
)

/**
 * Minimal Material 3 theme for Plin's first screens.
 */
@Composable
fun PlinTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        content = content,
    )
}
