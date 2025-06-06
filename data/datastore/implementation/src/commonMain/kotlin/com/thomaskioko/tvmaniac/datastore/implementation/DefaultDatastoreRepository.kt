package com.thomaskioko.tvmaniac.datastore.implementation

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineScope
import com.thomaskioko.tvmaniac.datastore.api.AppTheme
import com.thomaskioko.tvmaniac.datastore.api.AuthState
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultDatastoreRepository(
    private val coroutineScope: AppCoroutineScope,
    private val dataStore: DataStore<Preferences>,
) : DatastoreRepository {

    override fun saveTheme(appTheme: AppTheme) {
        coroutineScope.io.launch {
            dataStore.edit { preferences -> preferences[KEY_THEME] = appTheme.name }
        }
    }

    override fun observeTheme(): Flow<AppTheme> =
        dataStore.data.map { preferences ->
            when (preferences[KEY_THEME]) {
                AppTheme.LIGHT_THEME.name -> AppTheme.LIGHT_THEME
                AppTheme.DARK_THEME.name -> AppTheme.DARK_THEME
                else -> AppTheme.SYSTEM_THEME
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

    override fun observeAuthState(): Flow<AuthState> =
        dataStore.data.map { preferences ->
            AuthState(
                accessToken = preferences[KEY_ACCESS_TOKEN],
                refreshToken = preferences[KEY_REFRESH_TOKEN],
                isAuthorized = preferences[KEY_IS_AUTHORIZED] ?: false,
            )
        }

    override suspend fun saveLanguage(languageCode: String) {
        coroutineScope.io.launch {
            dataStore.edit { preferences ->
                preferences[KEY_LANGUAGE] = languageCode
            }
        }
    }

    override fun observeLanguage(): Flow<String> =
        dataStore.data.map { preferences ->
            preferences[KEY_LANGUAGE] ?: "en"
        }

    companion object {
        val KEY_THEME = stringPreferencesKey("app_theme")
        val KEY_ACCESS_TOKEN = stringPreferencesKey("auth_state")
        val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val KEY_IS_AUTHORIZED = booleanPreferencesKey("isAuthorized")
        val KEY_LANGUAGE = stringPreferencesKey("app_language")
    }
}
