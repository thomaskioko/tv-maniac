package com.thomaskioko.tvmaniac.compose.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.datastore.api.AppTheme

val LightColorScheme = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    error = md_theme_light_error,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    outline = md_theme_light_outline,
)

val DarkColorScheme = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    error = md_theme_dark_error,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    outline = md_theme_dark_outline,
)

val TerminalColorScheme = darkColorScheme(
    primary = md_theme_terminal_primary,
    onPrimary = md_theme_terminal_onPrimary,
    primaryContainer = md_theme_terminal_primaryContainer,
    secondary = md_theme_terminal_secondary,
    onSecondary = md_theme_terminal_onSecondary,
    error = md_theme_terminal_error,
    background = md_theme_terminal_background,
    onBackground = md_theme_terminal_onBackground,
    surface = md_theme_terminal_surface,
    onSurface = md_theme_terminal_onSurface,
    surfaceVariant = md_theme_terminal_surfaceVariant,
    outline = md_theme_terminal_outline,
)

val AutumnColorScheme = lightColorScheme(
    primary = md_theme_autumn_primary,
    onPrimary = md_theme_autumn_onPrimary,
    primaryContainer = md_theme_autumn_primaryContainer,
    secondary = md_theme_autumn_secondary,
    onSecondary = md_theme_autumn_onSecondary,
    error = md_theme_autumn_error,
    background = md_theme_autumn_background,
    onBackground = md_theme_autumn_onBackground,
    surface = md_theme_autumn_surface,
    onSurface = md_theme_autumn_onSurface,
    outline = md_theme_autumn_outline,
)

val AquaColorScheme = darkColorScheme(
    primary = md_theme_aqua_primary,
    onPrimary = md_theme_aqua_onPrimary,
    primaryContainer = md_theme_aqua_primaryContainer,
    secondary = md_theme_aqua_secondary,
    onSecondary = md_theme_aqua_onSecondary,
    error = md_theme_aqua_error,
    background = md_theme_aqua_background,
    onBackground = md_theme_aqua_onBackground,
    surface = md_theme_aqua_surface,
    onSurface = md_theme_aqua_onSurface,
    surfaceVariant = md_theme_aqua_surfaceVariant,
    onSurfaceVariant = md_theme_aqua_onSurfaceVariant,
    outline = md_theme_aqua_outline,
)

fun AppTheme.toColorScheme(isSystemInDarkTheme: Boolean): ColorScheme = when (this) {
    AppTheme.LIGHT_THEME -> LightColorScheme
    AppTheme.DARK_THEME -> DarkColorScheme
    AppTheme.TERMINAL_THEME -> TerminalColorScheme
    AppTheme.AUTUMN_THEME -> AutumnColorScheme
    AppTheme.AQUA_THEME -> AquaColorScheme
    AppTheme.SYSTEM_THEME -> if (isSystemInDarkTheme) DarkColorScheme else LightColorScheme
}

@Composable
fun TvManiacTheme(
    appTheme: AppTheme = AppTheme.SYSTEM_THEME,
    content: @Composable () -> Unit,
) {
    val isSystemDark = isSystemInDarkTheme()
    val colorScheme = appTheme.toColorScheme(isSystemDark)
    val backgroundTheme = BackgroundTheme(
        color = colorScheme.surface,
        tonalElevation = 2.dp,
    )

    CompositionLocalProvider(
        LocalBackgroundTheme provides backgroundTheme,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = tvManiacTypography(),
            shapes = tvManiacShapes,
            content = content,
        )
    }
}
