package com.thomaskioko.tvmaniac.datastore.implementation

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.datastore.api.Theme
import com.thomaskioko.tvmaniac.util.model.AppCoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class DatastoreRepositoryImpl(
    private val coroutineScope: AppCoroutineScope,
    private val dataStore: DataStore<Preferences>,
) : DatastoreRepository {

    override fun saveTheme(theme: Theme) {
        coroutineScope.io.launch {
            dataStore.edit { preferences ->
                preferences[KEY_THEME] = theme.name
            }
        }
    }

    override fun observeTheme(): Flow<Theme> = dataStore.data.map { preferences ->
        when (preferences[KEY_THEME]) {
            Theme.LIGHT.name -> Theme.LIGHT
            Theme.DARK.name -> Theme.DARK
            else -> Theme.SYSTEM
        }
    }

    companion object {
        val KEY_THEME = stringPreferencesKey("app_theme")
    }
}
