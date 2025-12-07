package com.thomaskioko.tvmaniac.traktauth.implementation

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.thomaskioko.tvmaniac.traktauth.api.AuthState
import com.thomaskioko.tvmaniac.traktauth.api.AuthStore
import kotlinx.coroutines.flow.first
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.time.Instant

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class AndroidAuthStore(
    private val dataStore: DataStore<Preferences>,
) : AuthStore {

    override suspend fun get(): AuthState? {
        val prefs = dataStore.data.first()

        val accessToken = prefs[KEY_ACCESS_TOKEN]
        val refreshToken = prefs[KEY_REFRESH_TOKEN]
        val expiresAt = prefs[KEY_EXPIRES_AT]
        val tokenLifetimeSeconds = prefs[KEY_TOKEN_LIFETIME]

        return if (accessToken != null && refreshToken != null) {
            AuthState(
                accessToken = accessToken,
                refreshToken = refreshToken,
                isAuthorized = true,
                expiresAt = expiresAt?.let { Instant.fromEpochMilliseconds(it) },
                tokenLifetimeSeconds = tokenLifetimeSeconds,
            )
        } else {
            null
        }
    }

    override suspend fun save(state: AuthState) {
        dataStore.edit { prefs ->
            prefs[KEY_ACCESS_TOKEN] = state.accessToken
            prefs[KEY_REFRESH_TOKEN] = state.refreshToken
            state.expiresAt?.let { prefs[KEY_EXPIRES_AT] = it.toEpochMilliseconds() }
            state.tokenLifetimeSeconds?.let { prefs[KEY_TOKEN_LIFETIME] = it }
        }
    }

    override suspend fun clear() {
        dataStore.edit { prefs ->
            prefs.remove(KEY_ACCESS_TOKEN)
            prefs.remove(KEY_REFRESH_TOKEN)
            prefs.remove(KEY_EXPIRES_AT)
            prefs.remove(KEY_TOKEN_LIFETIME)
        }
    }

    public companion object {
        private val KEY_ACCESS_TOKEN = stringPreferencesKey("trakt_access_token")
        private val KEY_REFRESH_TOKEN = stringPreferencesKey("trakt_refresh_token")
        private val KEY_EXPIRES_AT = longPreferencesKey("trakt_expires_at")
        private val KEY_TOKEN_LIFETIME = longPreferencesKey("trakt_token_lifetime")
    }
}
