package com.thomaskioko.tvmaniac.compose.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.ScanlineOverlay
import com.thomaskioko.tvmaniac.compose.components.toScanlineConfiguration
import com.thomaskioko.tvmaniac.domain.theme.Theme

public val LightColorScheme: ColorScheme = lightColorScheme(
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
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
)

public val DarkColorScheme: ColorScheme = darkColorScheme(
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
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
)

public val TerminalColorScheme: ColorScheme = darkColorScheme(
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
    onSurfaceVariant = md_theme_terminal_onSurfaceVariant,
    outline = md_theme_terminal_outline,
)

public val AutumnColorScheme: ColorScheme = lightColorScheme(
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
    surfaceVariant = md_theme_autumn_surfaceVariant,
    onSurfaceVariant = md_theme_autumn_onSurfaceVariant,
    outline = md_theme_autumn_outline,
)

public val AquaColorScheme: ColorScheme = darkColorScheme(
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

public val AmberColorScheme: ColorScheme = darkColorScheme(
    primary = md_theme_amber_primary,
    onPrimary = md_theme_amber_onPrimary,
    primaryContainer = md_theme_amber_primaryContainer,
    secondary = md_theme_amber_secondary,
    onSecondary = md_theme_amber_onSecondary,
    error = md_theme_amber_error,
    background = md_theme_amber_background,
    onBackground = md_theme_amber_onBackground,
    surface = md_theme_amber_surface,
    onSurface = md_theme_amber_onSurface,
    surfaceVariant = md_theme_amber_surfaceVariant,
    onSurfaceVariant = md_theme_amber_onSurfaceVariant,
    outline = md_theme_amber_outline,
)

public val SnowColorScheme: ColorScheme = darkColorScheme(
    primary = md_theme_snow_primary,
    onPrimary = md_theme_snow_onPrimary,
    primaryContainer = md_theme_snow_primaryContainer,
    secondary = md_theme_snow_secondary,
    onSecondary = md_theme_snow_onSecondary,
    error = md_theme_snow_error,
    background = md_theme_snow_background,
    onBackground = md_theme_snow_onBackground,
    surface = md_theme_snow_surface,
    onSurface = md_theme_snow_onSurface,
    surfaceVariant = md_theme_snow_surfaceVariant,
    onSurfaceVariant = md_theme_snow_onSurfaceVariant,
    outline = md_theme_snow_outline,
)

public val CrimsonColorScheme: ColorScheme = darkColorScheme(
    primary = md_theme_crimson_primary,
    onPrimary = md_theme_crimson_onPrimary,
    primaryContainer = md_theme_crimson_primaryContainer,
    secondary = md_theme_crimson_secondary,
    onSecondary = md_theme_crimson_onSecondary,
    error = md_theme_crimson_error,
    background = md_theme_crimson_background,
    onBackground = md_theme_crimson_onBackground,
    surface = md_theme_crimson_surface,
    onSurface = md_theme_crimson_onSurface,
    surfaceVariant = md_theme_crimson_surfaceVariant,
    onSurfaceVariant = md_theme_crimson_onSurfaceVariant,
    outline = md_theme_crimson_outline,
)

internal fun Theme.toColorScheme(isSystemInDarkTheme: Boolean): ColorScheme = when (this) {
    Theme.LIGHT_THEME -> LightColorScheme
    Theme.DARK_THEME -> DarkColorScheme
    Theme.TERMINAL_THEME -> TerminalColorScheme
    Theme.AUTUMN_THEME -> AutumnColorScheme
    Theme.AQUA_THEME -> AquaColorScheme
    Theme.AMBER_THEME -> AmberColorScheme
    Theme.SNOW_THEME -> SnowColorScheme
    Theme.CRIMSON_THEME -> CrimsonColorScheme
    Theme.SYSTEM_THEME -> if (isSystemInDarkTheme) DarkColorScheme else LightColorScheme
}

@Immutable
public data class TvManiacColorScheme(
    public val material: ColorScheme,
    public val accent: Color,
    public val onAccent: Color,
    public val buttonBackground: Color,
    public val onButtonBackground: Color,
    public val success: Color,
    public val onSuccess: Color,
    public val syncing: Color,
    public val grey: Color,
    public val scrim: Color,
    public val onScrim: Color,
    public val onError: Color,
)

public val LightTvManiacColorScheme: TvManiacColorScheme = TvManiacColorScheme(
    material = LightColorScheme,
    accent = md_theme_light_accent,
    onAccent = md_theme_light_onAccent,
    buttonBackground = md_theme_light_secondary,
    onButtonBackground = md_theme_light_onSecondary,
    success = green,
    onSuccess = Color.White,
    syncing = syncing,
    grey = grey,
    scrim = Color.Black,
    onScrim = Color.White,
    onError = md_theme_light_onError,
)

public val DarkTvManiacColorScheme: TvManiacColorScheme = TvManiacColorScheme(
    material = DarkColorScheme,
    accent = md_theme_dark_accent,
    onAccent = md_theme_dark_onAccent,
    buttonBackground = md_theme_dark_secondary,
    onButtonBackground = md_theme_dark_onSecondary,
    success = green,
    onSuccess = Color.White,
    syncing = syncing,
    grey = grey,
    scrim = Color.Black,
    onScrim = Color.White,
    onError = md_theme_dark_onError,
)

public val TerminalTvManiacColorScheme: TvManiacColorScheme = TvManiacColorScheme(
    material = TerminalColorScheme,
    accent = md_theme_terminal_accent,
    onAccent = md_theme_terminal_onAccent,
    buttonBackground = md_theme_terminal_secondary,
    onButtonBackground = md_theme_terminal_onSecondary,
    success = green,
    onSuccess = Color.White,
    syncing = syncing,
    grey = grey,
    scrim = Color.Black,
    onScrim = Color.White,
    onError = md_theme_terminal_onError,
)

public val AutumnTvManiacColorScheme: TvManiacColorScheme = TvManiacColorScheme(
    material = AutumnColorScheme,
    accent = md_theme_autumn_accent,
    onAccent = md_theme_autumn_onAccent,
    buttonBackground = md_theme_autumn_secondary,
    onButtonBackground = md_theme_autumn_onSecondary,
    success = green,
    onSuccess = Color.White,
    syncing = syncing,
    grey = grey,
    scrim = Color.Black,
    onScrim = Color.White,
    onError = md_theme_autumn_onError,
)

public val AquaTvManiacColorScheme: TvManiacColorScheme = TvManiacColorScheme(
    material = AquaColorScheme,
    accent = md_theme_aqua_accent,
    onAccent = md_theme_aqua_onAccent,
    buttonBackground = md_theme_aqua_secondary,
    onButtonBackground = md_theme_aqua_onSecondary,
    success = green,
    onSuccess = Color.White,
    syncing = syncing,
    grey = grey,
    scrim = Color.Black,
    onScrim = Color.White,
    onError = md_theme_aqua_onError,
)

public val AmberTvManiacColorScheme: TvManiacColorScheme = TvManiacColorScheme(
    material = AmberColorScheme,
    accent = md_theme_amber_accent,
    onAccent = md_theme_amber_onAccent,
    buttonBackground = md_theme_amber_secondary,
    onButtonBackground = md_theme_amber_onSecondary,
    success = green,
    onSuccess = Color.White,
    syncing = syncing,
    grey = grey,
    scrim = Color.Black,
    onScrim = Color.White,
    onError = md_theme_amber_onError,
)

public val SnowTvManiacColorScheme: TvManiacColorScheme = TvManiacColorScheme(
    material = SnowColorScheme,
    accent = md_theme_snow_accent,
    onAccent = md_theme_snow_onAccent,
    buttonBackground = md_theme_snow_secondary,
    onButtonBackground = md_theme_snow_onSecondary,
    success = green,
    onSuccess = Color.White,
    syncing = syncing,
    grey = grey,
    scrim = Color.Black,
    onScrim = Color.White,
    onError = md_theme_snow_onError,
)

public val CrimsonTvManiacColorScheme: TvManiacColorScheme = TvManiacColorScheme(
    material = CrimsonColorScheme,
    accent = md_theme_crimson_accent,
    onAccent = md_theme_crimson_onAccent,
    buttonBackground = md_theme_crimson_secondary,
    onButtonBackground = md_theme_crimson_onSecondary,
    success = green,
    onSuccess = Color.White,
    syncing = syncing,
    grey = grey,
    scrim = Color.Black,
    onScrim = Color.White,
    onError = md_theme_crimson_onError,
)

internal fun Theme.toTvManiacColorScheme(isSystemInDarkTheme: Boolean): TvManiacColorScheme = when (this) {
    Theme.LIGHT_THEME -> LightTvManiacColorScheme
    Theme.DARK_THEME -> DarkTvManiacColorScheme
    Theme.TERMINAL_THEME -> TerminalTvManiacColorScheme
    Theme.AUTUMN_THEME -> AutumnTvManiacColorScheme
    Theme.AQUA_THEME -> AquaTvManiacColorScheme
    Theme.AMBER_THEME -> AmberTvManiacColorScheme
    Theme.SNOW_THEME -> SnowTvManiacColorScheme
    Theme.CRIMSON_THEME -> CrimsonTvManiacColorScheme
    Theme.SYSTEM_THEME -> if (isSystemInDarkTheme) DarkTvManiacColorScheme else LightTvManiacColorScheme
}

internal val LocalTvManiacColorScheme: ProvidableCompositionLocal<TvManiacColorScheme> =
    staticCompositionLocalOf { LightTvManiacColorScheme }

@Composable
public fun TvManiacTheme(
    appTheme: Theme = Theme.SYSTEM_THEME,
    windowWidthSizeClass: WindowWidthSizeClass = WindowWidthSizeClass.Compact,
    fontSizePercent: Int = 100,
    posterWidthScale: Float = 1f,
    landscapeWidthScale: Float = 1f,
    posterCornerRadius: Dp = 0.dp,
    content: @Composable () -> Unit,
) {
    val isSystemDark = isSystemInDarkTheme()
    val colorScheme = appTheme.toColorScheme(isSystemDark)
    val tvManiacColorScheme = appTheme.toTvManiacColorScheme(isSystemDark)
    val backgroundTheme = BackgroundTheme(
        color = colorScheme.surface,
        tonalElevation = 2.dp,
    )
    val scanlineConfig = appTheme.toScanlineConfiguration()
    val density = LocalDensity.current
    val scaledDensity = Density(
        density = density.density,
        fontScale = density.fontScale * fontSizePercent / 100f,
    )

    CompositionLocalProvider(
        LocalBackgroundTheme provides backgroundTheme,
        LocalWindowWidthSizeClass provides windowWidthSizeClass,
        LocalDensity provides scaledDensity,
        LocalPosterWidthScale provides posterWidthScale,
        LocalLandscapeWidthScale provides landscapeWidthScale,
        LocalPosterCornerRadius provides posterCornerRadius,
        LocalTvManiacColorScheme provides tvManiacColorScheme,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = tvManiacTypography(),
            shapes = tvManiacShapes,
        ) {
            Box {
                content()
                ScanlineOverlay(configuration = scanlineConfig)
            }
        }
    }
}

public object TvManiacTheme {
    public val colorScheme: TvManiacColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalTvManiacColorScheme.current
}
