package com.thomaskioko.tvmaniac.navigation

import com.thomaskioko.tvmaniac.datastore.api.Theme

sealed interface ThemeState

data object Loading : ThemeState
data class ThemeLoaded(
    val theme: Theme = Theme.SYSTEM,
) : ThemeState
