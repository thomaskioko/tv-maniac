package com.thomaskioko.tvmaniac.oauth.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.AccountAuthState
import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.accountmanager.api.AuthError
import com.thomaskioko.tvmaniac.accountmanager.api.AuthState
import com.thomaskioko.tvmaniac.accountmanager.api.TokenRefreshResult
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.oauth.api.AuthStateHolder
import com.thomaskioko.tvmaniac.oauth.api.AuthStore
import com.thomaskioko.tvmaniac.oauth.api.RefreshTokenResult
import com.thomaskioko.tvmaniac.oauth.api.TokenRefreshAction
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultAuthStateHolder(
    private val dispatchers: AppCoroutineDispatchers,
    private val authStore: AuthStore,
    private val dateTimeProvider: DateTimeProvider,
    private val datastoreRepository: DatastoreRepository,
    private val requestManagerRepository: RequestManagerRepository,
    private val logger: Logger,
) : AuthStateHolder {

    private class ProviderState {
        val authState = MutableStateFlow<AuthState?>(null)
        val authError = MutableStateFlow<AuthError?>(null)
        val loginEvents = MutableSharedFlow<Unit>(replay = 1, extraBufferCapacity = 1)
        var expiry: Instant = Instant.DISTANT_PAST
        val refreshMutex = Mutex()
    }

    private val states: Map<AccountProvider, ProviderState> =
        AccountProvider.entries.associateWith { ProviderState() }
    private val scope = CoroutineScope(SupervisorJob() + dispatchers.io)

    init {
        scope.launch {
            states.keys.forEach { provider ->
                authStore.get(provider)?.also { cache(provider, it) }
            }
        }
    }

    override fun state(provider: AccountProvider): Flow<AccountAuthState> =
        entry(provider).authState.map { state ->
            if (state?.isAuthorized == true) {
                AccountAuthState.LOGGED_IN
            } else {
                AccountAuthState.LOGGED_OUT
            }
        }

    override fun authState(provider: AccountProvider): Flow<AuthState?> = entry(provider).authState

    override fun authError(provider: AccountProvider): Flow<AuthError?> = entry(provider).authError

    override fun loginEvents(provider: AccountProvider): SharedFlow<Unit> = entry(provider).loginEvents.asSharedFlow()

    override fun isLoggedIn(provider: AccountProvider): Boolean = entry(provider).authState.value?.isAuthorized == true

    override suspend fun getAuthState(provider: AccountProvider): AuthState? {
        val entry = entry(provider)
        val cached = entry.authState.value
        if (cached != null && cached.isAuthorized && dateTimeProvider.now() < entry.expiry) {
            return cached
        }
        return withContext(dispatchers.io) {
            authStore.get(provider)
        }?.also { cache(provider, it) }
    }

    override suspend fun saveTokens(
        provider: AccountProvider,
        accessToken: String,
        refreshToken: String,
        expiresAtSeconds: Long,
    ) {
        requestManagerRepository.clearSyncRelatedRequests()

        val newAuthState = AuthState(
            accessToken = accessToken,
            refreshToken = refreshToken,
            isAuthorized = true,
            expiresAt = Instant.fromEpochSeconds(expiresAtSeconds),
        )

        updateAuthState(provider, newAuthState, persist = true)

        entry(provider).loginEvents.tryEmit(Unit)
    }

    override suspend fun refreshTokens(provider: AccountProvider, action: TokenRefreshAction?): TokenRefreshResult {
        val entry = entry(provider)
        return entry.refreshMutex.withLock {
            val refresh = action ?: return@withLock TokenRefreshResult.NotLoggedIn
            val currentState = getAuthState(provider) ?: return@withLock TokenRefreshResult.NotLoggedIn

            when (val result = refresh.invoke(currentState)) {
                is RefreshTokenResult.Success -> {
                    entry.authError.value = null
                    updateAuthState(provider, result.authState)
                    datastoreRepository.setLastTokenRefreshTimestamp(dateTimeProvider.nowMillis())
                    TokenRefreshResult.Success(result.authState)
                }
                is RefreshTokenResult.TokenExpired -> {
                    logger.warning(LOG_TAG, "Token expired/revoked - clearing auth state")
                    entry.authError.value = AuthError.TokenExpired
                    updateAuthState(provider, AuthState.Empty)
                    TokenRefreshResult.TokenRevoked
                }
                is RefreshTokenResult.NetworkError -> {
                    logger.warning(LOG_TAG, "Network error during token refresh: ${result.message}")
                    entry.authError.value = AuthError.NetworkError
                    TokenRefreshResult.NetworkError(result.message)
                }
                is RefreshTokenResult.Failed -> {
                    logger.error(LOG_TAG, "Token refresh failed: ${result.message}")
                    entry.authError.value = AuthError.Unknown
                    TokenRefreshResult.Failed(result.message)
                }
            }
        }
    }

    override suspend fun logout(provider: AccountProvider) {
        updateAuthState(provider, AuthState.Empty)
    }

    override suspend fun setAuthError(provider: AccountProvider, error: AuthError?) {
        entry(provider).authError.value = error
    }

    private fun entry(provider: AccountProvider): ProviderState = states.getValue(provider)

    private fun cache(provider: AccountProvider, authState: AuthState) {
        val entry = entry(provider)
        entry.authState.update { authState }
        entry.expiry = if (authState.isAuthorized) dateTimeProvider.now() + 1.hours else Instant.DISTANT_PAST
    }

    private suspend fun updateAuthState(provider: AccountProvider, authState: AuthState, persist: Boolean = true) {
        if (persist) {
            withContext(dispatchers.io) {
                if (authState.isAuthorized) {
                    authStore.save(provider, authState)
                } else {
                    authStore.clear(provider)
                }
            }
        }
        cache(provider, authState)
    }

    private companion object {
        private const val LOG_TAG = "AuthStateHolder"
    }
}
