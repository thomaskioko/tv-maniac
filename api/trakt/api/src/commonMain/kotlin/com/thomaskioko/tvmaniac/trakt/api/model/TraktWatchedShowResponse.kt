package com.thomaskioko.tvmaniac.trakt.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TraktWatchedShowResponse(
    @SerialName("plays") val plays: Long,
    @SerialName("last_watched_at") val lastWatchedAt: String? = null,
    @SerialName("last_updated_at") val lastUpdatedAt: String? = null,
    @SerialName("show") val show: TraktShowResponse,
    @SerialName("seasons") val seasons: List<TraktWatchedSeasonResponse>? = null,
)

@Serializable
public data class TraktWatchedSeasonResponse(
    @SerialName("number") val number: Long,
    @SerialName("episodes") val episodes: List<TraktWatchedEpisodeResponse> = emptyList(),
)

@Serializable
public data class TraktWatchedEpisodeResponse(
    @SerialName("number") val number: Long,
    @SerialName("last_watched_at") val lastWatchedAt: String? = null,
    @SerialName("plays") val plays: Long? = null,
)
