package com.thomaskioko.tvmaniac.base.model

data class TraktOAuthInfo(
    val clientId: String,
    val clientSecret: String,
    val redirectUri: String,
)
