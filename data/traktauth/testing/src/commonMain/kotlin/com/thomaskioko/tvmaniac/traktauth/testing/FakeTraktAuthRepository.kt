package com.thomaskioko.tvmaniac.traktauth.testing

import com.thomaskioko.tvmaniac.traktauth.api.AuthError
import com.thomaskioko.tvmaniac.traktauth.api.AuthState
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeTraktAuthRepository : TraktAuthRepository {

    private val _state = MutableStateFlow(TraktAuthState.LOGGED_OUT)
    private var authState: AuthState? = null
    private var refreshAuthState: AuthState? = null
    private var loginError: AuthError? = null
    private val _authError = MutableStateFlow<AuthError?>(null)
    private val _isAuthenticating = MutableStateFlow(false)

    override val state: Flow<TraktAuthState> = _state.asStateFlow()

    override val authError: Flow<AuthError?> = _authError.asStateFlow()

    override val isAuthenticating: Flow<Boolean> = _isAuthenticating.asStateFlow()

    suspend fun setState(traktAuthState: TraktAuthState) {
        _state.emit(traktAuthState)
    }

    fun setAuthState(authState: AuthState?) {
        this.authState = authState
    }

    fun setRefreshAuthState(authState: AuthState?) {
        this.refreshAuthState = authState
    }

    fun setLoginError(error: AuthError?) {
        this.loginError = error
    }

    suspend fun setIsAuthenticating(isAuthenticating: Boolean) {
        _isAuthenticating.emit(isAuthenticating)
    }

    override suspend fun getAuthState(): AuthState? = authState

    override suspend fun refreshTokens(): AuthState? = refreshAuthState

    override suspend fun logout() {
        _state.emit(TraktAuthState.LOGGED_OUT)
        authState = null
    }

    override suspend fun saveTokens(
        accessToken: String,
        refreshToken: String,
        expiresAtSeconds: Long?,
    ) {
        // For testing, just update the state
        _state.emit(TraktAuthState.LOGGED_IN)
    }

    override suspend fun setAuthError(error: AuthError?) {
        _authError.emit(error)
    }
}
