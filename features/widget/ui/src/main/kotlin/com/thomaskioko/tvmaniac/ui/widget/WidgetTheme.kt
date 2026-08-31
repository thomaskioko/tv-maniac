package com.thomaskioko.tvmaniac.ui.widget

import androidx.compose.runtime.Composable
import androidx.glance.GlanceTheme
import androidx.glance.material3.ColorProviders
import com.thomaskioko.tvmaniac.compose.theme.DarkColorScheme
import com.thomaskioko.tvmaniac.compose.theme.LightColorScheme

@Composable
internal fun WidgetTheme(content: @Composable () -> Unit) {
    GlanceTheme(
        colors = ColorProviders(light = LightColorScheme, dark = DarkColorScheme),
        content = content,
    )
}
