package com.thomaskioko.tvmaniac.settings

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import com.thomaskioko.tvmaniac.datastore.api.AppTheme

@Composable
fun AppTheme.shouldUseDarkColors(): Boolean {
    return when (this) {
        AppTheme.LIGHT_THEME -> false
        AppTheme.DARK_THEME -> true
        else -> isSystemInDarkTheme()
    }
}
