package com.thomaskioko.tvmaniac.oauth.api

import com.thomaskioko.tvmaniac.accountmanager.api.AuthState

public interface TokenRefreshAction {
    public suspend operator fun invoke(currentState: AuthState): RefreshTokenResult
}
