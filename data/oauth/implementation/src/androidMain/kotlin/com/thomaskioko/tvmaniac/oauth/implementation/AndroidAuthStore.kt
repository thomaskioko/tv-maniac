package com.thomaskioko.tvmaniac.oauth.implementation

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.thomaskioko.tvmaniac.accountmanager.api.AuthState
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.core.base.AppPreferencesDataStore
import com.thomaskioko.tvmaniac.core.base.DeviceLocalDataStore
import com.thomaskioko.tvmaniac.oauth.api.AuthStore
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Instant

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class AndroidAuthStore(
    @DeviceLocalDataStore private val deviceLocalDataStore: DataStore<Preferences>,
    @AppPreferencesDataStore private val appPreferencesDataStore: DataStore<Preferences>,
) : AuthStore {

    private val mutex = Mutex()

    override suspend fun get(provider: SyncProviderSource): AuthState? = mutex.withLock {
        deviceLocalDataStore.data.first().toAuthState(provider)?.let { return@withLock it }

        val storedInAppPreferences = appPreferencesDataStore.data.first().toAuthState(provider) ?: return@withLock null
        write(provider, storedInAppPreferences)
        storedInAppPreferences
    }

    override suspend fun save(provider: SyncProviderSource, state: AuthState): Unit = mutex.withLock {
        write(provider, state)
    }

    override suspend fun clear(provider: SyncProviderSource): Unit = mutex.withLock {
        deviceLocalDataStore.edit { it.removeTokens(provider) }
        appPreferencesDataStore.edit { it.removeTokens(provider) }
    }

    private suspend fun write(provider: SyncProviderSource, state: AuthState) {
        deviceLocalDataStore.edit { prefs ->
            prefs[accessTokenKey(provider)] = state.accessToken
            prefs[refreshTokenKey(provider)] = state.refreshToken
            state.expiresAt?.let { prefs[expiresAtKey(provider)] = it.toEpochMilliseconds() }
            state.tokenLifetimeSeconds?.let { prefs[tokenLifetimeKey(provider)] = it }
        }
        appPreferencesDataStore.edit { it.removeTokens(provider) }
    }

    private fun Preferences.toAuthState(provider: SyncProviderSource): AuthState? {
        val accessToken = this[accessTokenKey(provider)]
        val refreshToken = this[refreshTokenKey(provider)]

        return if (accessToken != null && refreshToken != null) {
            AuthState(
                accessToken = accessToken,
                refreshToken = refreshToken,
                isAuthorized = true,
                expiresAt = this[expiresAtKey(provider)]?.let { Instant.fromEpochMilliseconds(it) },
                tokenLifetimeSeconds = this[tokenLifetimeKey(provider)],
            )
        } else {
            null
        }
    }

    private fun MutablePreferences.removeTokens(provider: SyncProviderSource) {
        remove(accessTokenKey(provider))
        remove(refreshTokenKey(provider))
        remove(expiresAtKey(provider))
        remove(tokenLifetimeKey(provider))
    }

    private fun prefix(provider: SyncProviderSource): String = provider.name.lowercase()
    private fun accessTokenKey(provider: SyncProviderSource) = stringPreferencesKey("${prefix(provider)}_access_token")
    private fun refreshTokenKey(provider: SyncProviderSource) = stringPreferencesKey("${prefix(provider)}_refresh_token")
    private fun expiresAtKey(provider: SyncProviderSource) = longPreferencesKey("${prefix(provider)}_expires_at")
    private fun tokenLifetimeKey(provider: SyncProviderSource) = longPreferencesKey("${prefix(provider)}_token_lifetime")
}
