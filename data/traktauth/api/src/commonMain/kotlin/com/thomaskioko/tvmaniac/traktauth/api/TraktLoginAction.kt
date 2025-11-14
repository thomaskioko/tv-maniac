package com.thomaskioko.tvmaniac.traktauth.api

interface TraktLoginAction {
    val lastError: AuthError?

    suspend operator fun invoke(): AuthState?

    fun onTokensReceived(
        accessToken: String,
        refreshToken: String,
        expiresAtSeconds: Long?,
    )

    fun onError(error: AuthError)
}
