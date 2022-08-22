package com.thomaskioko.tvmaniac.trakt.api

import kotlinx.coroutines.flow.StateFlow
import net.openid.appauth.AuthState

interface TraktManager {

    val state: StateFlow<TraktAuthState>

    fun clearAuth()

    fun onNewAuthState(newState: AuthState)
}