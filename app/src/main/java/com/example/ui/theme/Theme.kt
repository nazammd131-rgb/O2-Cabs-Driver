package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = O2Yellow,
    onPrimary = O2TextOnLight, // Yellow needs high-contrast black text on it
    primaryContainer = O2YellowDark,
    onPrimaryContainer = O2TextOnDark,
    secondary = O2SurfaceDark,
    onSecondary = O2TextOnDark,
    background = O2DarkBg,
    onBackground = O2TextOnDark,
    surface = O2SurfaceDark,
    onSurface = O2TextOnDark,
    surfaceVariant = O2SurfaceDark,
    onSurfaceVariant = O2TextSecondary,
    outline = O2Yellow
)

private val LightColorScheme = lightColorScheme(
    primary = O2Yellow,
    onPrimary = O2TextOnLight,
    primaryContainer = O2YellowLight,
    onPrimaryContainer = O2TextOnLight,
    secondary = O2SurfaceLight,
    onSecondary = O2TextOnLight,
    background = O2LightBg,
    onBackground = O2TextOnLight,
    surface = O2SurfaceLight,
    onSurface = O2TextOnLight,
    surfaceVariant = O2LightBg,
    onSurfaceVariant = O2TextSecondary,
    outline = O2YellowDark
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // We disable dynamicColor here to ensure our Yellow & Black O2 Cabs branding remains intact
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
