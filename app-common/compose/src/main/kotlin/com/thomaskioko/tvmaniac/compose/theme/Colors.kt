package com.thomaskioko.tvmaniac.compose.theme

import androidx.compose.material.Colors
import androidx.compose.material.LocalElevationOverlay
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.Dp
import kotlin.math.max
import kotlin.math.min

val yellow300 = Color(0xFFFFD34F)
val yellow500 = Color(0xFFFFbe0a)

val blue500 = Color(0xFF0b4CFF)
val blue700 = Color(0xFF002adb)
val colorError = Color(0xFFFF440b)

val grey = Color(0xFF131313)
val grey900 = Color(0xFF202020)

val LightColors = lightColors(
    primary = yellow300,
    primaryVariant = yellow500,
    secondary = blue500,
    secondaryVariant = blue700,
    surface = Color.White,
    onPrimary = Color.White,
    background = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    error = colorError
)

val DarkColors = darkColors(
    primary = grey,
    primaryVariant = grey900,
    secondary = yellow300,
    secondaryVariant = yellow500,
    background = grey900,
    surface = grey900,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    error = colorError
).withBrandedSurface()

fun Colors.withBrandedSurface() = copy(
    surface = primary.copy(alpha = 0.08f)
        .compositeOver(this.surface),
)

/**
 * Return the fully opaque color that results from compositing [onSurface] atop [surface] with the
 * given [alpha]. Useful for situations where semi-transparent colors are undesirable.
 */
@Composable
fun Colors.compositedOnSurface(alpha: Float): Color {
    return onSurface.copy(alpha = alpha).compositeOver(surface)
}

/**
 * Calculates the color of an elevated `surface` in dark mode. Returns `surface` in light mode.
 */
@Composable
fun Colors.elevatedSurface(elevation: Dp): Color {
    return LocalElevationOverlay.current?.apply(
        color = this.surface,
        elevation = elevation
    ) ?: this.surface
}

@Composable
fun backgroundGradient(): List<Color> {
    return listOf(
        MaterialTheme.colors.surface,
        MaterialTheme.colors.surface.copy(alpha = 0.9F),
        MaterialTheme.colors.surface.copy(alpha = 0.8F),
        MaterialTheme.colors.surface.copy(alpha = 0.7F),
        Color.Transparent
    )
}

fun Color.contrastAgainst(background: Color): Float {
    val fg = if (alpha < 1f) compositeOver(background) else this

    val fgLuminance = fg.luminance() + 0.05f
    val bgLuminance = background.luminance() + 0.05f

    return max(fgLuminance, bgLuminance) / min(fgLuminance, bgLuminance)
}
