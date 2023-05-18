package com.thomaskioko.tvmaniac.traktauth.api

import kotlinx.coroutines.flow.StateFlow

interface TraktAuthRepository {

    val state: StateFlow<TraktAuthState>

    fun updateAuthState(authState: AuthState)

    fun clearAuth()

    fun onNewAuthState(newState: AuthState)
}
