package com.thomaskioko.tvmaniac.simkl.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class SimklRewatchItemsResponse(
    @SerialName("shows") val shows: List<SimklRewatchShow> = emptyList(),
)

@Serializable
public data class SimklRewatchShow(
    @SerialName("is_rewatch") val isRewatch: Boolean = false,
    @SerialName("rewatch_id") val rewatchId: Long? = null,
    @SerialName("rewatch_status") val rewatchStatus: String? = null,
    @SerialName("last_watched_at") val lastWatchedAt: String? = null,
    @SerialName("watched_episodes_count") val watchedEpisodesCount: Int? = null,
    @SerialName("show") val show: SimklShowEntry,
)

@Serializable
public data class SimklRewatchHistoryRequest(
    @SerialName("shows") val shows: List<SimklRewatchHistoryShow>,
)

@Serializable
public data class SimklRewatchHistoryShow(
    @SerialName("ids") val ids: SimklShowIds,
    @SerialName("is_rewatch") val isRewatch: Boolean = true,
    @SerialName("rewatch_id") val rewatchId: Long? = null,
    @SerialName("rewatch_status") val rewatchStatus: String = "active",
    @SerialName("seasons") val seasons: List<SimklRewatchHistorySeason>,
)

@Serializable
public data class SimklRewatchHistorySeason(
    @SerialName("number") val number: Int,
    @SerialName("episodes") val episodes: List<SimklRewatchHistoryEpisode>,
)

@Serializable
public data class SimklRewatchHistoryEpisode(
    @SerialName("number") val number: Int,
    @SerialName("watched_at") val watchedAt: String,
)

@Serializable
public data class SimklRewatchHistoryResponse(
    @SerialName("added") val added: SimklRewatchHistoryAdded? = null,
)

@Serializable
public data class SimklRewatchHistoryAdded(
    @SerialName("episodes") val episodes: Int? = null,
    @SerialName("statuses") val statuses: List<SimklRewatchHistoryStatus> = emptyList(),
)

@Serializable
public data class SimklRewatchHistoryStatus(
    @SerialName("response") val response: SimklRewatchHistoryStatusResponse,
)

@Serializable
public data class SimklRewatchHistoryStatusResponse(
    @SerialName("rewatch_id") val rewatchId: Long? = null,
    @SerialName("rewatch_status") val rewatchStatus: String? = null,
)
