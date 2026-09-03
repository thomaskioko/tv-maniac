package com.thomaskioko.tvmaniac.ui.widget

import androidx.compose.runtime.Composable
import androidx.glance.GlanceTheme
import androidx.glance.color.ColorProviders
import androidx.glance.material3.ColorProviders
import com.thomaskioko.tvmaniac.compose.theme.DarkColorScheme
import com.thomaskioko.tvmaniac.compose.theme.LightColorScheme
import com.thomaskioko.tvmaniac.compose.theme.toColorScheme
import com.thomaskioko.tvmaniac.datastore.api.AppTheme
import com.thomaskioko.tvmaniac.domain.theme.Theme

@Composable
internal fun WidgetTheme(theme: AppTheme, content: @Composable () -> Unit) {
    GlanceTheme(
        colors = theme.toColorProviders(),
        content = content,
    )
}

private fun AppTheme.toColorProviders(): ColorProviders = when (this) {
    AppTheme.SYSTEM_THEME -> ColorProviders(light = LightColorScheme, dark = DarkColorScheme)
    else -> ColorProviders(Theme.valueOf(name).toColorScheme(isSystemInDarkTheme = false))
}
