package com.thomaskioko.tvmaniac.compose.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.theme.LocalBackgroundTheme
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme

/**
 * The main background for the app. Uses [LocalBackgroundTheme] to set the color and tonal elevation
 * of a [Surface].
 *
 * @param modifier Modifier to be applied to the background.
 * @param content The background content.
 */
@Composable
public fun TvManiacBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val color = LocalBackgroundTheme.current.color
    val tonalElevation = LocalBackgroundTheme.current.tonalElevation

    Surface(
        color = if (color == Color.Unspecified) Color.Transparent else color,
        tonalElevation = if (tonalElevation == Dp.Unspecified) 0.dp else tonalElevation,
        modifier = modifier.fillMaxSize(),
    ) {
        CompositionLocalProvider(LocalAbsoluteTonalElevation provides 0.dp) { content() }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, name = "Light Theme", showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Theme", showBackground = true)
public annotation class ThemePreviews

@ThemePreviews
@Composable
private fun BackgroundDefault() {
    TvManiacTheme { TvManiacBackground(Modifier.size(100.dp), content = {}) }
}
