package com.thomaskioko.tvmaniac.traktauth.testing

import com.thomaskioko.tvmaniac.datastore.api.AuthState
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeTraktAuthRepository : TraktAuthRepository {

    override val state: StateFlow<TraktAuthState>
        get() = MutableStateFlow(TraktAuthState.LOGGED_OUT)

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