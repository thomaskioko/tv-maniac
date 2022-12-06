package com.thomaskioko.tvmaniac.settings.api

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun saveTheme(theme: String)
    fun observeTheme(): Flow<Theme>
}

enum class Theme {
    LIGHT,
    DARK,
    SYSTEM
}