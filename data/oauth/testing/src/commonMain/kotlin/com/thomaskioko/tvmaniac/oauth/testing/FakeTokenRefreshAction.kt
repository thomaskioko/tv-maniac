package com.thomaskioko.tvmaniac.oauth.testing

import com.thomaskioko.tvmaniac.accountmanager.api.AuthState
import com.thomaskioko.tvmaniac.oauth.api.RefreshTokenResult
import com.thomaskioko.tvmaniac.oauth.api.TokenRefreshAction

public class FakeTokenRefreshAction(
    private var result: RefreshTokenResult = RefreshTokenResult.TokenExpired,
) : TokenRefreshAction {
    public fun setResult(result: RefreshTokenResult) {
        this.result = result
    }

    override suspend fun invoke(currentState: AuthState): RefreshTokenResult = result
}
