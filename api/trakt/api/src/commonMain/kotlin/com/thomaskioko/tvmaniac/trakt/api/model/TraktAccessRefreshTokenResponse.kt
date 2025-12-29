package com.thomaskioko.tvmaniac.trakt.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TraktAccessRefreshTokenResponse(
    @SerialName("scope") val scope: String?,
    @SerialName("access_token") val accessToken: String?,
    @SerialName("created_at") val createdAt: Long?,
    @SerialName("expires_in") val expiresIn: Long?,
    @SerialName("refresh_token") val refreshToken: String?,
    @SerialName("token_type") val tokenType: String?,
)
