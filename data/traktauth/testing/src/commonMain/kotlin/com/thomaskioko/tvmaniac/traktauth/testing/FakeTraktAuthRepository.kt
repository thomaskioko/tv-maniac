package com.thomaskioko.tvmaniac.traktauth.testing

import com.thomaskioko.tvmaniac.traktauth.api.AuthState
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeTraktAuthRepository : TraktAuthRepository {

    private val _state = MutableStateFlow(TraktAuthState.LOGGED_OUT)
    private var authState: AuthState? = null

    override val state: Flow<TraktAuthState> = _state.asStateFlow()

    suspend fun setState(traktAuthState: TraktAuthState) {
        _state.emit(traktAuthState)
    }

    fun setAuthState(authState: AuthState?) {
        this.authState = authState
    }

    override suspend fun getAuthState(): AuthState? = authState

    override suspend fun login(): AuthState? {
        _state.emit(TraktAuthState.LOGGED_IN)
        return authState
    }

    override suspend fun refreshTokens(): AuthState? = authState

    override suspend fun logout() {
        _state.emit(TraktAuthState.LOGGED_OUT)
        authState = null
    }
}
