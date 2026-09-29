package com.example.deansgateproject.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkObsidianColorScheme = darkColorScheme(
    primary = VibrantVioletPrimary,
    onPrimary = TextWhite,
    primaryContainer = PillActiveContainer,
    onPrimaryContainer = TextWhite,
    secondary = VibrantVioletSecondary,
    onSecondary = TextWhite,
    secondaryContainer = PillInactiveContainer,
    onSecondaryContainer = TextSubtitle,
    tertiary = VibrantVioletLight,
    onTertiary = TextWhite,
    background = DeepObsidianBackground,
    onBackground = TextWhite,
    surface = DarkVioletSurface,
    onSurface = TextWhite,
    surfaceVariant = DarkVioletSurfaceVariant,
    onSurfaceVariant = TextMuted,
    surfaceContainer = DarkVioletSurface,
    surfaceContainerLow = DeepObsidianBackgroundSecondary,
    surfaceContainerHigh = DarkVioletSurfaceVariant,
    outline = CardBorderColor,
    outlineVariant = DividerColor,
    error = Color(0xFFEF4444),
    onError = TextWhite,
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = TextWhite
)

@Composable
fun DeansgateProjectTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkObsidianColorScheme,
        typography = Typography,
        content = content
    )
}
