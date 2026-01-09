package com.thomaskioko.tvmaniac.traktauth.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.traktauth.api.AuthError
import com.thomaskioko.tvmaniac.traktauth.api.AuthState
import com.thomaskioko.tvmaniac.traktauth.api.AuthStore
import com.thomaskioko.tvmaniac.traktauth.api.RefreshTokenResult
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import com.thomaskioko.tvmaniac.traktauth.api.TraktRefreshTokenAction
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultTraktAuthRepository(
    private val dispatchers: AppCoroutineDispatchers,
    private val authStore: AuthStore,
    private val refreshTokenAction: Lazy<TraktRefreshTokenAction>,
    private val dateTimeProvider: DateTimeProvider,
) : TraktAuthRepository {

    private val authState = MutableStateFlow<AuthState?>(null)
    private var authStateExpiry: Instant = Instant.DISTANT_PAST
    private val scope = CoroutineScope(SupervisorJob() + dispatchers.io)

    private val _authError = MutableStateFlow<AuthError?>(null)

    init {
        scope.launch {
            val savedState = authStore.get()
            if (savedState != null && savedState.isAuthorized) {
                cacheAuthState(savedState)
            }
        }
    }

    override val state: Flow<TraktAuthState> = authState.map { state ->
        when (state?.isAuthorized) {
            true -> TraktAuthState.LOGGED_IN
            else -> TraktAuthState.LOGGED_OUT
        }
    }

    override val authError: Flow<AuthError?> = _authError

    override fun isLoggedIn(): Boolean = authState.value?.isAuthorized == true

    override suspend fun getAuthState(): AuthState? {
        val cached = authState.value

        if (cached != null && cached.isAuthorized && dateTimeProvider.now() < authStateExpiry) {
            return cached
        }

        return withContext(dispatchers.io) {
            authStore.get()
        }?.also { cacheAuthState(it) }
    }

    override suspend fun refreshTokens(): AuthState? {
        val currentState = getAuthState() ?: return null

        return when (val result = refreshTokenAction.value.invoke(currentState)) {
            is RefreshTokenResult.Success -> {
                updateAuthState(result.authState)
                result.authState
            }
            is RefreshTokenResult.TokenExpired -> {
                updateAuthState(AuthState.Empty)
                null
            }
            is RefreshTokenResult.Failed -> null
        }
    }

    override suspend fun logout() {
        updateAuthState(AuthState.Empty)
    }

    override suspend fun saveTokens(
        accessToken: String,
        refreshToken: String,
        expiresAtSeconds: Long,
    ) {
        val authState = AuthState(
            accessToken = accessToken,
            refreshToken = refreshToken,
            isAuthorized = true,
            expiresAt = Instant.fromEpochSeconds(expiresAtSeconds),
        )

        updateAuthState(authState, persist = true)
    }

    override suspend fun setAuthError(error: AuthError?) {
        _authError.value = error
    }

    private fun cacheAuthState(authState: AuthState) {
        this.authState.update { authState }
        authStateExpiry = when {
            authState.isAuthorized -> dateTimeProvider.now() + 1.hours
            else -> Instant.DISTANT_PAST
        }
    }

    private suspend fun updateAuthState(authState: AuthState, persist: Boolean = true) {
        if (persist) {
            withContext(dispatchers.io) {
                if (authState.isAuthorized) {
                    authStore.save(authState)
                } else {
                    authStore.clear()
                }
            }
        }

        cacheAuthState(authState)
    }
}
