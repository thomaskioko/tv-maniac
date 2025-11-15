package com.thomaskioko.tvmaniac.traktauth.testing

import com.thomaskioko.tvmaniac.traktauth.api.AuthState
import com.thomaskioko.tvmaniac.traktauth.api.TraktRefreshTokenAction

class FakeTraktRefreshTokenAction : TraktRefreshTokenAction {
    private var result: AuthState? = null

    fun setResult(authState: AuthState?) {
        result = authState
    }

    override suspend fun invoke(currentState: AuthState): AuthState? = result
}
