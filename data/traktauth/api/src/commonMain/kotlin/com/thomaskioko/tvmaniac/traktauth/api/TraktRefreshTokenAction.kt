package com.thomaskioko.tvmaniac.traktauth.api

import com.thomaskioko.tvmaniac.accountmanager.api.AuthState

public interface TraktRefreshTokenAction {
    public suspend operator fun invoke(currentState: AuthState): RefreshTokenResult
}
