package com.thomaskioko.tvmaniac.datastore.implementation

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.thomaskioko.tvmaniac.datastore.api.AuthState
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.datastore.api.Theme
import com.thomaskioko.tvmaniac.util.model.AppCoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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

    override suspend fun saveAuthState(authState: AuthState) {
        coroutineScope.io.launch {
            dataStore.edit { preferences ->
                preferences[KEY_ACCESS_TOKEN] = authState.accessToken!!
                preferences[KEY_REFRESH_TOKEN] = authState.refreshToken!!
                preferences[KEY_IS_AUTHORIZED] = authState.isAuthorized
            }
        }
    }

    override suspend fun getAuthState(): AuthState? {
        return if (dataStore.data.first()[KEY_ACCESS_TOKEN] == null) {
            null
        } else {
            AuthState(
                accessToken = dataStore.data.first()[KEY_ACCESS_TOKEN],
                refreshToken = dataStore.data.first()[KEY_REFRESH_TOKEN],
                isAuthorized = dataStore.data.first()[KEY_IS_AUTHORIZED] ?: false,
            )
        }
    }

    override fun clearAuthState() {
        coroutineScope.io.launch {
            dataStore.edit {
                it.remove(KEY_ACCESS_TOKEN)
                it.remove(KEY_REFRESH_TOKEN)
                it.remove(KEY_IS_AUTHORIZED)
            }
        }
    }

    override fun observeAuthState(): Flow<AuthState> = dataStore.data.map { preferences ->
        AuthState(
            accessToken = preferences[KEY_ACCESS_TOKEN],
            refreshToken = preferences[KEY_REFRESH_TOKEN],
            isAuthorized = preferences[KEY_IS_AUTHORIZED] ?: false,
        )
    }

    companion object {
        val KEY_THEME = stringPreferencesKey("app_theme")
        val KEY_ACCESS_TOKEN = stringPreferencesKey("auth_state")
        val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val KEY_IS_AUTHORIZED = booleanPreferencesKey("isAuthorized")
    }
}
