package com.thomaskioko.tvmaniac.compose.theme

import androidx.compose.material.Colors
import androidx.compose.material.LocalElevationOverlay
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.Dp
import kotlin.math.max
import kotlin.math.min

val colorPrimaryLight = Color(0xFFFFBE0B)
val colorPrimaryVariantLight = Color(0xFFF1B000)
val colorSecondaryLight = Color(0xFF00284d)
val colorSecondaryVariantLight = Color(0xFFFFD258)
val colorError = Color(0xFFFC2d11)

val colorPrimaryDark = Color(0xFF131313)
val colorPrimaryVariantDark = Color(0xFF202020)
val colorSecondaryDark = Color(0xFFFFBE0B)
val colorSecondaryVariantDark = Color(0xFFF1B000)


val listGradient = listOf(Color(0xFFFFd258), colorPrimaryLight, colorPrimaryVariantLight)


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
