package com.summer.gpacalculator.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = LightAccentEnd,
    secondary = LightAccentStart,
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = LightCard,
    outlineVariant = LightDivider,
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkAccentStart,
    secondary = DarkAccentEnd,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkCard,
    outlineVariant = DarkDivider,
)

@Composable
fun GpaCalculatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography(),
        content = content,
    )
}

