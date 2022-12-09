package com.thomaskioko.tvmaniac.settings.api

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class FakeSettingsRepository : SettingsRepository {

    private val themeFlow = MutableSharedFlow<Theme>(replay = 0)

    suspend fun setTheme(theme: Theme) {
        themeFlow.emit(theme)
    }

    override fun saveTheme(theme: Theme) {

    }

    override fun observeTheme(): Flow<Theme> = themeFlow.asSharedFlow()
}