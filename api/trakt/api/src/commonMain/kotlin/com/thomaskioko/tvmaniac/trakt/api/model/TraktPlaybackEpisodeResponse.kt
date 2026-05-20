package com.thomaskioko.tvmaniac.trakt.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TraktPlaybackEpisodeResponse(
    @SerialName("id") val id: Long,
    @SerialName("progress") val progress: Double,
    @SerialName("paused_at") val pausedAt: String,
    @SerialName("type") val type: String,
    @SerialName("episode") val episode: TraktNextEpisodeResponse,
    @SerialName("show") val show: TraktShowResponse,
)
