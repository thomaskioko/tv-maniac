package com.thomaskioko.tvmaniac.settings.implementation

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.thomaskioko.tvmaniac.settings.api.SettingsRepository
import com.thomaskioko.tvmaniac.settings.api.Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SettingsRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
    private val coroutineScope: CoroutineScope
) : SettingsRepository {

    override fun saveTheme(theme: String) {
        coroutineScope.launch {
            dataStore.edit { settings ->
                settings[KEY_THEME] = theme
            }
        }
    }

    override fun observeTheme(): Flow<Theme> = dataStore.data.map { theme ->
        when (theme[KEY_THEME]) {
            "light" -> Theme.LIGHT
            "dark" -> Theme.DARK
            else -> Theme.SYSTEM
        }
    }

    companion object {
        val KEY_THEME = stringPreferencesKey("app_theme")
    }
}