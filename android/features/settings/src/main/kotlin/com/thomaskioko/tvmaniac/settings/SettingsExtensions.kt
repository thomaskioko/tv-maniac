package com.thomaskioko.tvmaniac.settings

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.datastore.api.Theme

@Composable
fun DatastoreRepository.shouldUseDarkColors(): Boolean {
    val themePreference = observeTheme().collectAsState(initial = Theme.SYSTEM)
    return when (themePreference.value) {
        Theme.LIGHT -> false
        Theme.DARK -> true
        else -> isSystemInDarkTheme()
    }
}
