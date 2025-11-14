package com.thomaskioko.tvmaniac.traktauth.api

interface TraktRefreshTokenAction {
    suspend operator fun invoke(currentState: AuthState): AuthState?
}
