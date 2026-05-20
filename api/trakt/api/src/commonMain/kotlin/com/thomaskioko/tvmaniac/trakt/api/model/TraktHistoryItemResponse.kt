package com.thomaskioko.tvmaniac.trakt.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TraktHistoryItemResponse(
    @SerialName("id") val id: Long,
    @SerialName("watched_at") val watchedAt: String,
    @SerialName("action") val action: String,
    @SerialName("type") val type: String,
    @SerialName("episode") val episode: TraktNextEpisodeResponse? = null,
    @SerialName("show") val show: TraktShowResponse? = null,
)
