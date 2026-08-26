package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = BrandAmber,
    onPrimary = NavyDark,
    primaryContainer = NavyLight,
    onPrimaryContainer = Color.White,
    secondary = TealAccent,
    onSecondary = Color.White,
    background = NavyDark,
    surface = NavySurface,
    onBackground = Color.White,
    onSurface = Color.White,
    surfaceVariant = NavyLight,
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF475569)
)

private val LightColorScheme = lightColorScheme(
    primary = BrandAmber,
    onPrimary = NavyDark,
    primaryContainer = Color(0xFFFEF3C7),
    onPrimaryContainer = Color(0xFF78350F),
    secondary = NavyDark,
    onSecondary = Color.White,
    secondaryContainer = CreamCardVariant,
    onSecondaryContainer = NavyDark,
    tertiary = TealAccent,
    onTertiary = Color.White,
    background = CreamBackground,
    surface = CreamSurface,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    surfaceVariant = CreamCardVariant,
    onSurfaceVariant = TextSecondary,
    outline = CreamBorder,
    error = CoralError,
    onError = Color.White
)

@Composable
fun EasyTransportTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    EasyTransportTheme(darkTheme = darkTheme, content = content)
}
