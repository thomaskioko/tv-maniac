package com.thomaskioko.tvmaniac.datastore.api

data class AuthState(
    val accessToken: String? = "",
    val refreshToken: String? = "",
    val isAuthorized: Boolean = false,
)