package com.thomaskioko.tvmaniac.datastore.implementation

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.datastore.api.Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class DatastoreRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
    private val coroutineScope: CoroutineScope
) : DatastoreRepository {

    override fun saveTheme(theme: Theme) {
        coroutineScope.launch {
            dataStore.edit { settings ->
                settings[KEY_THEME] = theme.name
            }
        }
    }

    override fun observeTheme(): Flow<Theme> = dataStore.data.map { theme ->
        when (theme[KEY_THEME]) {
            Theme.LIGHT.name -> Theme.LIGHT
            Theme.DARK.name -> Theme.DARK
            else -> Theme.SYSTEM
        }
    }

    companion object {
        val KEY_THEME = stringPreferencesKey("app_theme")
    }
}