package com.thomaskioko.tvmaniac.settings.api

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class FakeSettingsRepository : SettingsRepository {

    private val themeFlow : Channel<Theme> = Channel(Channel.UNLIMITED)

    suspend fun setTheme(theme: Theme) {
        themeFlow.send(theme)
    }

    override fun saveTheme(theme: Theme) { }

    override fun observeTheme(): Flow<Theme> = themeFlow.receiveAsFlow()
}