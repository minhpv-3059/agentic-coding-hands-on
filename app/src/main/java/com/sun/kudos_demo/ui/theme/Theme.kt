package com.sun.kudos_demo.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val KudosColorScheme = darkColorScheme(
    primary = KudosGold,
    onPrimary = KudosDarkText,
    primaryContainer = KudosSecondaryButtonNormal,
    onPrimaryContainer = KudosGold,
    secondary = KudosBorder,
    onSecondary = KudosDarkText,
    background = KudosBackground,
    onBackground = KudosWhite,
    surface = KudosContainer,
    onSurface = KudosWhite,
    surfaceVariant = KudosContainer2,
    onSurfaceVariant = KudosGray,
    outline = KudosBorder,
    outlineVariant = KudosDivider,
    error = KudosError,
    onError = KudosWhite,
    scrim = KudosBackground
)

@Composable
fun KudosAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = KudosColorScheme,
        typography = KudosTypography,
        content = content
    )
}
