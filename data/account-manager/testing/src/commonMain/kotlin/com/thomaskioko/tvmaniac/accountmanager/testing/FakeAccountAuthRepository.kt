package com.thomaskioko.tvmaniac.accountmanager.testing

import com.thomaskioko.tvmaniac.accountmanager.api.AccountAuthRepository
import com.thomaskioko.tvmaniac.accountmanager.api.AccountAuthState
import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.accountmanager.api.AuthError
import com.thomaskioko.tvmaniac.accountmanager.api.AuthState
import com.thomaskioko.tvmaniac.accountmanager.api.TokenRefreshResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

public class FakeAccountAuthRepository(
    override val provider: AccountProvider,
) : AccountAuthRepository {

    private val _state = MutableStateFlow(AccountAuthState.LOGGED_OUT)
    private val _authState = MutableStateFlow<AuthState?>(null)
    private val _authError = MutableStateFlow<AuthError?>(null)
    private val _loginEvents = MutableSharedFlow<Unit>(replay = 1, extraBufferCapacity = 1)
    private var refreshOutcome: TokenRefreshResult = TokenRefreshResult.NotLoggedIn

    public fun setState(state: AccountAuthState) {
        _state.value = state
    }

    public fun setAuthState(authState: AuthState?) {
        _authState.value = authState
    }

    public fun setAuthErrorValue(error: AuthError?) {
        _authError.value = error
    }

    public fun triggerLogin() {
        _loginEvents.tryEmit(Unit)
    }

    public fun setRefreshOutcome(outcome: TokenRefreshResult) {
        refreshOutcome = outcome
    }

    override val state: Flow<AccountAuthState> = _state
    override val authState: Flow<AuthState?> = _authState
    override val authError: Flow<AuthError?> = _authError
    override val loginEvents: SharedFlow<Unit> = _loginEvents.asSharedFlow()

    override fun isLoggedIn(): Boolean = _state.value == AccountAuthState.LOGGED_IN

    override suspend fun logout() {
        _state.value = AccountAuthState.LOGGED_OUT
    }

    override suspend fun refreshTokens(): TokenRefreshResult = refreshOutcome

    override suspend fun setAuthError(error: AuthError?) {
        _authError.value = error
    }
}
