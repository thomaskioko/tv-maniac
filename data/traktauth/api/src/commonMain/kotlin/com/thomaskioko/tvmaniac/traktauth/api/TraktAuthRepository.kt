package com.thomaskioko.tvmaniac.traktauth.api

import kotlinx.coroutines.flow.Flow

interface TraktAuthRepository {

    val state: Flow<TraktAuthState>

    val authError: Flow<AuthError?>

    val isAuthenticating: Flow<Boolean>

    suspend fun getAuthState(): AuthState?

    suspend fun refreshTokens(): AuthState?

    suspend fun logout()

    suspend fun saveTokens(
        accessToken: String,
        refreshToken: String,
        expiresAtSeconds: Long?,
    )

    suspend fun setAuthError(error: AuthError?)
}
