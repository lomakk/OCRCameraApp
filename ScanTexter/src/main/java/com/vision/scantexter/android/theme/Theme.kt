package com.vision.scantexter.android.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightThemeColors = lightColors(
    primary = Color.Black,
    primaryVariant = Color.Black.copy(alpha = 0.8f),
    onPrimary = Color.White,
    secondary = Color.White,
    secondaryVariant = Color.White,
    onSecondary = Color.Black,
    error = Color.Red,
    onBackground = Color.Black
)

private val DarkThemeColors = darkColors(
    primary = Color.White,
    primaryVariant = Color.White.copy(alpha = 0.8f),
    onPrimary = Color.Black,
    secondary = Color.Black,
    secondaryVariant = Color.Black,
    onSecondary = Color.White,
    error = Color.Red,
    onBackground = Color.White
)

@Composable
fun ScanTexterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (darkTheme) DarkThemeColors else LightThemeColors, // Fix if need to support dark theme palette
        typography = MaterialTheme.typography,
        shapes = AppShapes,
        content = content
    )
}
