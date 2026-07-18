package com.thomaskioko.tvmaniac.compose.theme

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * Current window width size class, provided once at the root from `calculateWindowSizeClass`.
 * Defaults to [WindowWidthSizeClass.Compact] for previews and tests.
 */
public val LocalWindowWidthSizeClass: ProvidableCompositionLocal<WindowWidthSizeClass> =
    staticCompositionLocalOf { WindowWidthSizeClass.Compact }

/**
 * Size multiplier for portrait poster cards, provided from the user's poster style preference.
 * Scales [Layout.posterWidth] and derives [Layout.posterColumns]. Defaults to 1.0 (Standard),
 * which reproduces the responsive base sizes exactly.
 */
public val LocalPosterWidthScale: ProvidableCompositionLocal<Float> =
    staticCompositionLocalOf { 1f }

/**
 * Size multiplier for landscape (16:9 backdrop) cards, provided from the user's poster style
 * preference. Scales [Layout.backdropCardWidth]. Defaults to 1.0 (Standard).
 */
public val LocalLandscapeWidthScale: ProvidableCompositionLocal<Float> =
    staticCompositionLocalOf { 1f }

/**
 * Corner radius applied to every poster, provided from the user's poster style preference.
 * Defaults to 0.dp (Sharp), reproducing the current square-cornered posters.
 */
public val LocalPosterCornerRadius: ProvidableCompositionLocal<Dp> =
    staticCompositionLocalOf { 0.dp }

public object Layout {
    public val bodyMargin: Dp
        @Composable @ReadOnlyComposable
        get() = when (LocalWindowWidthSizeClass.current) {
            WindowWidthSizeClass.Medium -> 24.dp
            WindowWidthSizeClass.Expanded -> 32.dp
            else -> 16.dp
        }

    public val gutter: Dp
        @Composable @ReadOnlyComposable
        get() = when (LocalWindowWidthSizeClass.current) {
            WindowWidthSizeClass.Medium -> 12.dp
            WindowWidthSizeClass.Expanded -> 16.dp
            else -> 8.dp
        }

    public val posterColumns: Int
        @Composable @ReadOnlyComposable
        get() {
            val base = when (LocalWindowWidthSizeClass.current) {
                WindowWidthSizeClass.Medium -> 5
                WindowWidthSizeClass.Expanded -> 7
                else -> 3
            }
            return (base / LocalPosterWidthScale.current).roundToInt().coerceAtLeast(1)
        }

    /**
     * Responsive poster width that ignores the user's poster size preference. Used by list-row
     * thumbnails and other fixed-layout surfaces that should not resize with the preference.
     */
    public val posterWidthFixed: Dp
        @Composable @ReadOnlyComposable
        get() = when (LocalWindowWidthSizeClass.current) {
            WindowWidthSizeClass.Medium -> 140.dp
            WindowWidthSizeClass.Expanded -> 160.dp
            else -> 112.dp
        }

    public val posterWidth: Dp
        @Composable @ReadOnlyComposable
        get() = posterWidthFixed * LocalPosterWidthScale.current

    public val backdropCardWidth: Dp
        @Composable @ReadOnlyComposable
        get() {
            val base = when (LocalWindowWidthSizeClass.current) {
                WindowWidthSizeClass.Medium -> 300.dp
                WindowWidthSizeClass.Expanded -> 360.dp
                else -> 240.dp
            }
            return base * LocalLandscapeWidthScale.current
        }

    public val castCardWidth: Dp
        @Composable @ReadOnlyComposable
        get() = when (LocalWindowWidthSizeClass.current) {
            WindowWidthSizeClass.Medium -> 128.dp
            WindowWidthSizeClass.Expanded -> 144.dp
            else -> 112.dp
        }
}

/**
 * Fixed image tokens that do not depend on the window size class: aspect ratios (expressed as
 * width / height, matching `Modifier.aspectRatio` semantics) and hero/parallax heights.
 */
public object ImageDimens {
    public val PosterAspect: Float = 2f / 3f
    public val BackdropAspect: Float = 16f / 9f
    public val CastAspect: Float = 3f / 4f

    public val GridItemSpacing: Dp = 4.dp

    public val HeroDefaultHeight: Dp = 520.dp
    public val HeroShowDetailsHeight: Dp = 500.dp
    public val HeroProfileHeight: Dp = 350.dp
    public val HeroCollapsedHeight: Dp = 120.dp
}

public object TvManiacSpacing {
    public val none: Dp = 0.dp
    public val xxxSmall: Dp = 2.dp
    public val xxSmall: Dp = 4.dp
    public val xSmall: Dp = 8.dp
    public val small: Dp = 12.dp
    public val medium: Dp = 16.dp
    public val large: Dp = 24.dp
    public val xLarge: Dp = 32.dp
    public val xxLarge: Dp = 48.dp
    public val xxxLarge: Dp = 64.dp
}

public object TvManiacElevation {
    public val small: Dp = 2.dp
    public val medium: Dp = 4.dp
    public val large: Dp = 8.dp
}
