package com.thomaskioko.tvmaniac.settings.api

import kotlinx.coroutines.flow.Flow

interface TvManiacPreferences {

    fun setup()
    fun observeTheme(): Flow<Theme>
    fun emitTheme(themeValue: String)

    enum class Theme {
        LIGHT,
        DARK,
        SYSTEM
    }
}
