package com.thomaskioko.tvmaniac.oauth.implementation

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.KeychainSettings
import com.thomaskioko.tvmaniac.accountmanager.api.AuthState
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.oauth.api.AuthStore
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.withContext
import platform.Foundation.CFBridgingRetain
import platform.Security.kSecAttrAccessible
import platform.Security.kSecAttrAccessibleAfterFirstUnlock
import platform.Security.kSecAttrService
import kotlin.time.Instant

@OptIn(
    ExperimentalSettingsApi::class,
    ExperimentalSettingsImplementation::class,
    ExperimentalForeignApi::class,
)
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class IosAuthStore(
    private val dispatchers: AppCoroutineDispatchers,
) : AuthStore {

    private val settings by lazy {
        KeychainSettings(
            kSecAttrService to CFBridgingRetain("com.thomaskioko.tvmaniac.oauth"),
            kSecAttrAccessible to kSecAttrAccessibleAfterFirstUnlock,
        )
    }

    override suspend fun get(provider: SyncProviderSource): AuthState? = withContext(dispatchers.io) {
        val accessToken = settings.getStringOrNull(accessTokenKey(provider))
        val refreshToken = settings.getStringOrNull(refreshTokenKey(provider))
        val expiresAt = settings.getLongOrNull(expiresAtKey(provider))
        val tokenLifetimeSeconds = settings.getLongOrNull(tokenLifetimeKey(provider))

        if (accessToken != null && refreshToken != null) {
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

    override suspend fun save(provider: SyncProviderSource, state: AuthState) {
        withContext(dispatchers.io) {
            settings.putString(accessTokenKey(provider), state.accessToken)
            settings.putString(refreshTokenKey(provider), state.refreshToken)
            state.expiresAt?.let { settings.putLong(expiresAtKey(provider), it.toEpochMilliseconds()) }
            state.tokenLifetimeSeconds?.let { settings.putLong(tokenLifetimeKey(provider), it) }
        }
    }

    override suspend fun clear(provider: SyncProviderSource) {
        withContext(dispatchers.io) {
            settings.remove(accessTokenKey(provider))
            settings.remove(refreshTokenKey(provider))
            settings.remove(expiresAtKey(provider))
            settings.remove(tokenLifetimeKey(provider))
        }
    }

    private fun prefix(provider: SyncProviderSource): String = provider.name.lowercase()
    private fun accessTokenKey(provider: SyncProviderSource) = "${prefix(provider)}_access_token"
    private fun refreshTokenKey(provider: SyncProviderSource) = "${prefix(provider)}_refresh_token"
    private fun expiresAtKey(provider: SyncProviderSource) = "${prefix(provider)}_expires_at"
    private fun tokenLifetimeKey(provider: SyncProviderSource) = "${prefix(provider)}_token_lifetime"
}
