package com.thomaskioko.tvmaniac.traktauth.testing

import com.thomaskioko.tvmaniac.datastore.api.AuthState
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeTraktAuthRepository : TraktAuthRepository {

    private val state = MutableStateFlow(TraktAuthState.LOGGED_OUT)

    suspend fun setAuthState(authState: TraktAuthState) {
        state.emit(authState)
    }

    override fun observeState(): StateFlow<TraktAuthState> = state.asStateFlow()

    override fun updateAuthState(authState: AuthState) {
        // no-op
    }

    override fun clearAuth() {
        // no-op
    }

    override fun onNewAuthState(newState: AuthState) {
        // no-op
    }
}
