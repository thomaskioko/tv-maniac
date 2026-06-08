package com.thomaskioko.tvmaniac.oauth.implementation

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.accountmanager.api.AuthState
import com.thomaskioko.tvmaniac.core.base.AppPreferencesDataStore
import com.thomaskioko.tvmaniac.oauth.api.AuthStore
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.first
import kotlin.time.Instant

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class AndroidAuthStore(
    @AppPreferencesDataStore private val dataStore: DataStore<Preferences>,
) : AuthStore {

    override suspend fun get(provider: AccountProvider): AuthState? {
        val prefs = dataStore.data.first()

        val accessToken = prefs[accessTokenKey(provider)]
        val refreshToken = prefs[refreshTokenKey(provider)]
        val expiresAt = prefs[expiresAtKey(provider)]
        val tokenLifetimeSeconds = prefs[tokenLifetimeKey(provider)]

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

    override suspend fun save(provider: AccountProvider, state: AuthState) {
        dataStore.edit { prefs ->
            prefs[accessTokenKey(provider)] = state.accessToken
            prefs[refreshTokenKey(provider)] = state.refreshToken
            state.expiresAt?.let { prefs[expiresAtKey(provider)] = it.toEpochMilliseconds() }
            state.tokenLifetimeSeconds?.let { prefs[tokenLifetimeKey(provider)] = it }
        }
    }

    override suspend fun clear(provider: AccountProvider) {
        dataStore.edit { prefs ->
            prefs.remove(accessTokenKey(provider))
            prefs.remove(refreshTokenKey(provider))
            prefs.remove(expiresAtKey(provider))
            prefs.remove(tokenLifetimeKey(provider))
        }
    }

    private fun prefix(provider: AccountProvider): String = provider.name.lowercase()
    private fun accessTokenKey(provider: AccountProvider) = stringPreferencesKey("${prefix(provider)}_access_token")
    private fun refreshTokenKey(provider: AccountProvider) = stringPreferencesKey("${prefix(provider)}_refresh_token")
    private fun expiresAtKey(provider: AccountProvider) = longPreferencesKey("${prefix(provider)}_expires_at")
    private fun tokenLifetimeKey(provider: AccountProvider) = longPreferencesKey("${prefix(provider)}_token_lifetime")
}
