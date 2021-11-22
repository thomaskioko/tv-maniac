package com.thomaskioko.tvmaniac.settings.domain

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.thomaskioko.tvmaniac.settings.api.TvManiacPreferences
import com.thomaskioko.tvmaniac.settings.api.TvManiacPreferences.Theme

@Composable
fun TvManiacPreferences.shouldUseDarkColors(): Boolean {
    val themePreference = observeTheme().collectAsState(initial = Theme.SYSTEM)
    return when (themePreference.value) {
        Theme.LIGHT -> false
        Theme.DARK -> true
        else -> isSystemInDarkTheme()
    }
}
