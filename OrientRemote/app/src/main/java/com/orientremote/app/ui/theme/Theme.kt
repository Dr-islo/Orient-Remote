package com.orientremote.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/** User-selectable theme preference, persisted via [com.orientremote.app.data.local.PreferencesManager]. */
enum class ThemeMode {
    DARK, LIGHT, SYSTEM;

    companion object {
        fun fromNameOrNull(name: String?): ThemeMode? = entries.find { it.name == name }
    }
}

private val OrientDarkColorScheme = darkColorScheme(
    primary = AccentCyan,
    onPrimary = DarkBackground,
    secondary = AccentCyanDim,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onBackground = DarkOnSurface,
    onSurface = DarkOnSurface,
    onSurfaceVariant = DarkOnSurfaceMuted,
    error = DangerRed
)

private val OrientLightColorScheme = lightColorScheme(
    primary = AccentBlue,
    onPrimary = LightSurface,
    secondary = AccentBlue,
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = LightSurfaceVariant,
    onBackground = LightOnSurface,
    onSurface = LightOnSurface,
    onSurfaceVariant = LightOnSurfaceMuted,
    error = DangerRed
)

@Composable
fun OrientRemoteTheme(
    themeMode: ThemeMode = ThemeMode.DARK,
    content: @Composable () -> Unit
) {
    val useDarkTheme = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = if (useDarkTheme) OrientDarkColorScheme else OrientLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = OrientTypography,
        content = content
    )
}
