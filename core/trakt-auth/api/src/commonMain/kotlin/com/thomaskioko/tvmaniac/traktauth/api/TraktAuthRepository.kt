package com.thomaskioko.tvmaniac.traktauth.api

import com.thomaskioko.tvmaniac.datastore.api.AuthState
import kotlinx.coroutines.flow.StateFlow

interface TraktAuthRepository {

    fun observeState(): StateFlow<TraktAuthState>

    fun updateAuthState(authState: AuthState)

    fun clearAuth()

    fun onNewAuthState(newState: AuthState)
}
