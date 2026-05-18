package com.thomaskioko.tvmaniac.trakt.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TraktWatchedShowResponse(
    @SerialName("plays") val plays: Long,
    @SerialName("last_watched_at") val lastWatchedAt: String,
    @SerialName("last_updated_at") val lastUpdatedAt: String,
    @SerialName("reset_at") val resetAt: String? = null,
    @SerialName("show") val show: TraktShowResponse,
)
