package com.example.medreminders.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = Navy,
    onPrimary = Surface,
    primaryContainer = NavyLight,
    onPrimaryContainer = Navy,

    secondary = Green,
    onSecondary = Surface,
    secondaryContainer = GreenLight,
    onSecondaryContainer = Green,

    error = Red,
    onError = Surface,
    errorContainer = RedLight,
    onErrorContainer = Red,

    background = Background,
    onBackground = TextPrimary,

    surface = Surface,
    onSurface = TextPrimary,

    surfaceVariant = NavyLight,
    onSurfaceVariant = TextMuted,

    outline = Border,
)

@Composable
fun MedRemindersTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = AppTypography,
        content = content,
    )
}