package com.thomaskioko.tvmanic.trakt.implementation.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RefreshAccessTokenBody(
    @SerialName("refresh_token") val refreshToken: String?,
    @SerialName("client_id") val clientId: String = "",
    @SerialName("client_secret") val clientSecret: String = "",
    @SerialName("redirect_uri") val redirectUri: String = "",
    @SerialName("grant_type") val grantType: String = "refresh_token"
)