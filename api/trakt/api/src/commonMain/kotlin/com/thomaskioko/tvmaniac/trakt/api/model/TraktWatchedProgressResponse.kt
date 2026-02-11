package com.thomaskioko.tvmaniac.trakt.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TraktWatchedProgressResponse(
    @SerialName("aired") val aired: Int,
    @SerialName("completed") val completed: Int,
    @SerialName("last_watched_at") val lastWatchedAt: String? = null,
    @SerialName("reset_at") val resetAt: String? = null,
    @SerialName("next_episode") val nextEpisode: TraktNextEpisodeResponse? = null,
    @SerialName("last_episode") val lastEpisode: TraktNextEpisodeResponse? = null,
)
