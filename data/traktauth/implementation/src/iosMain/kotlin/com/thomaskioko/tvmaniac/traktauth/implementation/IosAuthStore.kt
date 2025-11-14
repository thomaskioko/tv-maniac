package com.thomaskioko.tvmaniac.traktauth.implementation

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.KeychainSettings
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.traktauth.api.AuthState
import com.thomaskioko.tvmaniac.traktauth.api.AuthStore
import com.thomaskioko.tvmaniac.traktauth.api.SimpleAuthState
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import platform.Foundation.CFBridgingRetain
import platform.Security.kSecAttrAccessible
import platform.Security.kSecAttrAccessibleAfterFirstUnlock
import platform.Security.kSecAttrService
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.time.Instant

@OptIn(
    ExperimentalSettingsApi::class,
    ExperimentalSettingsImplementation::class,
    ExperimentalForeignApi::class,
)
@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class IosAuthStore(
    private val dispatchers: AppCoroutineDispatchers,
) : AuthStore {

    private val settings by lazy {
        KeychainSettings(
            kSecAttrService to CFBridgingRetain("com.thomaskioko.tvmaniac.traktauth"),
            kSecAttrAccessible to kSecAttrAccessibleAfterFirstUnlock,
        )
    }

    override suspend fun get(): AuthState? = withContext(dispatchers.io) {
        val accessToken = settings.getStringOrNull(KEY_ACCESS_TOKEN)
        val refreshToken = settings.getStringOrNull(KEY_REFRESH_TOKEN)
        val expiresAt = settings.getLongOrNull(KEY_EXPIRES_AT)

        if (accessToken != null && refreshToken != null) {
            SimpleAuthState(
                accessToken = accessToken,
                refreshToken = refreshToken,
                isAuthorized = true,
                expiresAt = expiresAt?.let { Instant.fromEpochMilliseconds(it) },
            )
        } else {
            null
        }
    }

    override suspend fun save(state: AuthState) {
        withContext(dispatchers.io) {
            settings.putString(KEY_ACCESS_TOKEN, state.accessToken)
            settings.putString(KEY_REFRESH_TOKEN, state.refreshToken)
            state.expiresAt?.let { settings.putLong(KEY_EXPIRES_AT, it.toEpochMilliseconds()) }
        }
    }

    override suspend fun clear() {
        withContext(dispatchers.io) {
            settings.remove(KEY_ACCESS_TOKEN)
            settings.remove(KEY_REFRESH_TOKEN)
            settings.remove(KEY_EXPIRES_AT)
        }
    }

    companion object {
        private const val KEY_ACCESS_TOKEN = "trakt_access_token"
        private const val KEY_REFRESH_TOKEN = "trakt_refresh_token"
        private const val KEY_EXPIRES_AT = "trakt_expires_at"
    }
}
