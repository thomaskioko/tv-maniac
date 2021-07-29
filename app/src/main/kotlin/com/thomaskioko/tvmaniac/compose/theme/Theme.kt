package com.thomaskioko.tvmaniac.compose.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


private val LightColors = lightColors(
    primary = yellow,
    primaryVariant = yellow600,
    secondary = blue,
    secondaryVariant = yellow300,
    surface = Color.White,
    onPrimary = Color.White,
    background = Color.White,
    onSecondary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black,
    error = colorError
)

private val DarkColors = darkColors(
    primary = grey,
    primaryVariant = grey900,
    secondary = yellow600,
    secondaryVariant = yellow700,
    background = grey900,
    surface = grey900,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    error = colorError
)


@Composable
fun TvManiacTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (darkTheme) DarkColors else LightColors,
        typography = TvManiacTypography,
        shapes = shapes,
        content = content
    )
}
