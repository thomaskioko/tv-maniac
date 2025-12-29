package com.thomaskioko.tvmaniac.traktauth.testing

import com.thomaskioko.tvmaniac.traktauth.api.AuthState
import com.thomaskioko.tvmaniac.traktauth.api.RefreshTokenResult
import com.thomaskioko.tvmaniac.traktauth.api.TraktRefreshTokenAction

public class FakeTraktRefreshTokenAction : TraktRefreshTokenAction {
    private var result: RefreshTokenResult = RefreshTokenResult.TokenExpired

    public fun setResult(authState: RefreshTokenResult) {
        result = authState
    }

    override suspend fun invoke(currentState: AuthState): RefreshTokenResult = result
}
