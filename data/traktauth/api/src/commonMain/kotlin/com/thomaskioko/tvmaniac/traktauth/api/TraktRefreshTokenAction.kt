package com.thomaskioko.tvmaniac.traktauth.api

public interface TraktRefreshTokenAction {
    public suspend operator fun invoke(currentState: AuthState): RefreshTokenResult
}
