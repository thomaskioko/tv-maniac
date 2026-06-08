package com.thomaskioko.tvmaniac.traktauth.testing

import com.thomaskioko.tvmaniac.accountmanager.api.AuthState
import com.thomaskioko.tvmaniac.oauth.api.RefreshTokenResult
import com.thomaskioko.tvmaniac.oauth.api.TokenRefreshAction

public class FakeTraktRefreshTokenAction : TokenRefreshAction {
    private var result: RefreshTokenResult = RefreshTokenResult.TokenExpired

    public fun setResult(authState: RefreshTokenResult) {
        result = authState
    }

    override suspend fun invoke(currentState: AuthState): RefreshTokenResult = result
}
