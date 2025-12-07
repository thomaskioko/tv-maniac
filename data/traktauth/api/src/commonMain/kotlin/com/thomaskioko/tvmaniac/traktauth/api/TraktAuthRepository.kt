package com.thomaskioko.tvmaniac.traktauth.api

import kotlinx.coroutines.flow.Flow

public interface TraktAuthRepository {

    public val state: Flow<TraktAuthState>

    public val authError: Flow<AuthError?>

    public suspend fun getAuthState(): AuthState?

    public suspend fun refreshTokens(): AuthState?

    public suspend fun logout()

    public suspend fun saveTokens(
        accessToken: String,
        refreshToken: String,
        expiresAtSeconds: Long,
    )

    public suspend fun setAuthError(error: AuthError?)
}
