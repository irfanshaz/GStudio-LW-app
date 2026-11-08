package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val WonderLightColorScheme = lightColorScheme(
    primary = ActionContinueBlue,
    onPrimary = PlaySurfaceWhite,
    primaryContainer = CalmSkyGradientTop,
    onPrimaryContainer = DeepSlateInk,
    secondary = ActionPlayGreen,
    onSecondary = PlaySurfaceWhite,
    secondaryContainer = SoftCreamCard,
    onSecondaryContainer = DeepSlateInk,
    tertiary = ActionListenAmber,
    onTertiary = DeepSlateInk,
    background = CalmCoolBlueBg,
    onBackground = DeepSlateInk,
    surface = PlaySurfaceWhite,
    onSurface = DeepSlateInk,
    surfaceVariant = SoftCreamCard,
    onSurfaceVariant = MutedSlateText
)

@Composable
fun LittleWonderWorldTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = WonderLightColorScheme,
        typography = Typography,
        content = content
    )
}
