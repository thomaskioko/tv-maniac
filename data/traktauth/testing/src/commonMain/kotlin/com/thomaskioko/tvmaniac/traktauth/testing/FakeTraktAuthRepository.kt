package com.thomaskioko.tvmaniac.traktauth.testing

import com.thomaskioko.tvmaniac.accountmanager.api.AccountAuthState
import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.accountmanager.api.AuthError
import com.thomaskioko.tvmaniac.accountmanager.api.AuthState
import com.thomaskioko.tvmaniac.accountmanager.api.TokenRefreshResult
import com.thomaskioko.tvmaniac.oauth.api.OAuthRepository
import com.thomaskioko.tvmaniac.traktauth.implementation.TraktOAuthRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class, replaces = [TraktOAuthRepository::class])
public class FakeTraktAuthRepository : OAuthRepository {

    override val provider: AccountProvider = AccountProvider.TRAKT

    private val _state = MutableStateFlow(AccountAuthState.LOGGED_OUT)
    private val _authState = MutableStateFlow<AuthState?>(null)
    private var refreshOutcome: TokenRefreshResult = TokenRefreshResult.NotLoggedIn
    private val _authError = MutableStateFlow<AuthError?>(null)
    private val _loginEvents = MutableSharedFlow<Unit>(replay = 1, extraBufferCapacity = 1)

    public suspend fun setState(traktAuthState: AccountAuthState) {
        _state.emit(traktAuthState)
    }

    public fun setAuthState(state: AuthState?) {
        _authState.value = state
    }

    public fun setRefreshOutcome(outcome: TokenRefreshResult) {
        refreshOutcome = outcome
    }

    /**
     * Emits a login event in tests without going through the real OAuth handshake.
     * Mirrors what `DefaultTraktAuthRepository.saveTokens` does on a successful sign-in.
     */
    public fun triggerLogin() {
        _loginEvents.tryEmit(Unit)
    }

    override val state: Flow<AccountAuthState> = _state.asStateFlow()

    override val authState: Flow<AuthState?> = _authState.asStateFlow()

    override val authError: Flow<AuthError?> = _authError.asStateFlow()

    override val loginEvents: SharedFlow<Unit> = _loginEvents.asSharedFlow()

    override fun isLoggedIn(): Boolean = _state.value == AccountAuthState.LOGGED_IN

    override suspend fun getAuthState(): AuthState? = _authState.value

    override suspend fun refreshTokens(): TokenRefreshResult = refreshOutcome

    override suspend fun logout() {
        _state.emit(AccountAuthState.LOGGED_OUT)
        _authState.value = null
    }

    override suspend fun saveTokens(
        accessToken: String,
        refreshToken: String,
        expiresAtSeconds: Long,
    ) {
        _state.emit(AccountAuthState.LOGGED_IN)
        _loginEvents.tryEmit(Unit)
    }

    override suspend fun setAuthError(error: AuthError?) {
        _authError.emit(error)
    }
}
