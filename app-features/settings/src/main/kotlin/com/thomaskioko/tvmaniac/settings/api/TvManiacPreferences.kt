package com.thomaskioko.tvmaniac.settings.api

import com.thomaskioko.tvmaniac.presentation.contract.Theme
import kotlinx.coroutines.flow.Flow

interface TvManiacPreferences {

    fun setup()
    fun observeTheme(): Flow<Theme>
    fun emitTheme(themeValue: String)
}
