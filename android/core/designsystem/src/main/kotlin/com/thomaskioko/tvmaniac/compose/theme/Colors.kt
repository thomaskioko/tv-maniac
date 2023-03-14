package com.thomaskioko.tvmaniac.compose.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import kotlin.math.max
import kotlin.math.min

val green = Color(0xFF00b300)

val md_theme_light_primary = Color(0xFF1646F7)
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_secondary = Color(0xFF3947EA)
val md_theme_light_onSecondary = Color(0xFFFFFFFF)
val md_theme_light_error = Color(0xFFBA1A1A)
val md_theme_light_background = Color(0xFFF8FDFF)
val md_theme_light_onBackground = Color(0xFF001F25)
val md_theme_light_surface = Color(0xFFF8FDFF)
val md_theme_light_onSurface = Color(0xFF1F2123)


val md_theme_dark_primary = Color(0xFF1F2123)
val md_theme_dark_onPrimary = Color(0xFFE0E0FF)
val md_theme_dark_secondary = Color(0xFFFCBC04)
val md_theme_dark_onSecondary = Color(0xFFE0E0FF)
val md_theme_dark_error = Color(0xFFBA1A1A)
val md_theme_dark_background = Color(0xFF2c2c30)
val md_theme_dark_onBackground = Color(0xFFE0E0FF)
val md_theme_dark_surface = Color(0xFF1F2123)
val md_theme_dark_onSurface = Color(0xFFF8FDFF)

@Composable
fun backgroundGradient(): List<Color> {
    return listOf(
        MaterialTheme.colorScheme.surface,
        MaterialTheme.colorScheme.surface.copy(alpha = 0.9F),
        MaterialTheme.colorScheme.surface.copy(alpha = 0.8F),
        MaterialTheme.colorScheme.surface.copy(alpha = 0.7F),
        Color.Transparent
    )
}

fun Color.contrastAgainst(background: Color): Float {
    val fg = if (alpha < 1f) compositeOver(background) else this

    val fgLuminance = fg.luminance() + 0.05f
    val bgLuminance = background.luminance() + 0.05f

    return max(fgLuminance, bgLuminance) / min(fgLuminance, bgLuminance)
}
