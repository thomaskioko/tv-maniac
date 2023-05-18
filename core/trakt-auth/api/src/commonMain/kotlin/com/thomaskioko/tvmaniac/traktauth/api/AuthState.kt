package com.thomaskioko.tvmaniac.traktauth.api

data class AuthState(
    val accessToken: String,
    val refreshToken: String,
    val isAuthorized: Boolean,
)
