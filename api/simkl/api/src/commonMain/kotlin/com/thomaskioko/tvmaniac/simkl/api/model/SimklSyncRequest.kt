package com.thomaskioko.tvmaniac.simkl.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class SimklSyncHistoryRequest(
    @SerialName("shows") val shows: List<SimklHistoryShow>,
)

@Serializable
public data class SimklHistoryShow(
    @SerialName("ids") val ids: SimklShowIds,
    @SerialName("seasons") val seasons: List<SimklHistorySeason>,
)

@Serializable
public data class SimklHistorySeason(
    @SerialName("number") val number: Int,
    @SerialName("watched_at") val watchedAt: String? = null,
    @SerialName("episodes") val episodes: List<SimklHistoryEpisode>,
)

@Serializable
public data class SimklHistoryEpisode(
    @SerialName("number") val number: Int,
)
