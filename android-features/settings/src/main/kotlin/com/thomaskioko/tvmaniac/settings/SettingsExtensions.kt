package com.thomaskioko.tvmaniac.settings

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import com.thomaskioko.tvmaniac.datastore.api.Theme

@Composable
fun Theme.shouldUseDarkColors(): Boolean {
    return when (this) {
        Theme.LIGHT -> false
        Theme.DARK -> true
        else -> isSystemInDarkTheme()
    }
}
