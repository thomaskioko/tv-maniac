package com.thomaskioko.tvmaniac.oauth.testing

import com.thomaskioko.tvmaniac.accountmanager.api.AccountAuthState
import com.thomaskioko.tvmaniac.accountmanager.api.AuthError
import com.thomaskioko.tvmaniac.accountmanager.api.AuthState
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.accountmanager.api.TokenRefreshResult
import com.thomaskioko.tvmaniac.oauth.api.AuthStateHolder
import com.thomaskioko.tvmaniac.oauth.api.TokenRefreshAction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

public class FakeAuthStateHolder : AuthStateHolder {
    private val authStates = MutableStateFlow<Map<SyncProviderSource, AuthState>>(emptyMap())
    private val loginEvents = MutableSharedFlow<Unit>()
    private var refreshResult: TokenRefreshResult = TokenRefreshResult.NotLoggedIn

    public fun setAuthState(provider: SyncProviderSource, state: AuthState?) {
        authStates.value = if (state == null) authStates.value - provider else authStates.value + (provider to state)
    }

    public fun setRefreshResult(result: TokenRefreshResult) {
        refreshResult = result
    }

    override fun state(provider: SyncProviderSource): Flow<AccountAuthState> =
        authStates.map { if (provider in it) AccountAuthState.LOGGED_IN else AccountAuthState.LOGGED_OUT }

    override fun authState(provider: SyncProviderSource): Flow<AuthState?> = authStates.map { it[provider] }

    override fun authError(provider: SyncProviderSource): Flow<AuthError?> = flowOf(null)

    override fun loginEvents(provider: SyncProviderSource): SharedFlow<Unit> = loginEvents.asSharedFlow()

    override fun isLoggedIn(provider: SyncProviderSource): Boolean = provider in authStates.value

    override suspend fun getAuthState(provider: SyncProviderSource): AuthState? = authStates.value[provider]

    override suspend fun saveTokens(
        provider: SyncProviderSource,
        accessToken: String,
        refreshToken: String,
        expiresAtSeconds: Long,
    ) {
        setAuthState(provider, AuthState(accessToken = accessToken, refreshToken = refreshToken))
    }

    override suspend fun refreshTokens(provider: SyncProviderSource, action: TokenRefreshAction?): TokenRefreshResult = refreshResult

    override suspend fun logout(provider: SyncProviderSource) {
        setAuthState(provider, null)
    }

    override suspend fun setAuthError(provider: SyncProviderSource, error: AuthError?) {
    }
}
