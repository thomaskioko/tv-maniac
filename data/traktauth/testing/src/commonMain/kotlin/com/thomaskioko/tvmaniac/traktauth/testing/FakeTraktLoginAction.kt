package com.thomaskioko.tvmaniac.traktauth.testing

import com.thomaskioko.tvmaniac.traktauth.api.AuthError
import com.thomaskioko.tvmaniac.traktauth.api.AuthState
import com.thomaskioko.tvmaniac.traktauth.api.TraktLoginAction

class FakeTraktLoginAction : TraktLoginAction {
    private var result: AuthState? = null
    private var authError: AuthError? = null

    override val lastError: AuthError?
        get() = authError

    fun setResult(authState: AuthState?) {
        result = authState
    }

    fun setError(error: AuthError?) {
        authError = error
    }

    override suspend fun invoke(): AuthState? = result

    override fun onTokensReceived(
        accessToken: String,
        refreshToken: String,
        expiresAtSeconds: Long?,
    ) {
    }

    override fun onError(error: AuthError) {
        authError = error
    }
}
