package com.thomaskioko.tvmaniac.simkl.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class SimklAllItemsResponse(
    @SerialName("shows") val shows: List<SimklWatchedShow> = emptyList(),
)

@Serializable
public data class SimklAddHistoryResponse(
    @SerialName("added") val added: SimklHistoryCountBucket? = null,
    @SerialName("not_found") val notFound: SimklNotFoundBucket? = null,
)

@Serializable
public data class SimklRemoveHistoryResponse(
    @SerialName("deleted") val deleted: SimklHistoryCountBucket? = null,
    @SerialName("not_found") val notFound: SimklNotFoundBucket? = null,
)

@Serializable
public data class SimklHistoryCountBucket(
    @SerialName("movies") val movies: Int? = null,
    @SerialName("shows") val shows: Int? = null,
    @SerialName("episodes") val episodes: Int? = null,
)

@Serializable
public data class SimklNotFoundBucket(
    @SerialName("movies") val movies: List<SimklShowEntry> = emptyList(),
    @SerialName("shows") val shows: List<SimklShowEntry> = emptyList(),
    @SerialName("episodes") val episodes: List<SimklShowEntry> = emptyList(),
)

@Serializable
public data class SimklWatchedShow(
    @SerialName("status") val status: String? = null,
    @SerialName("last_watched_at") val lastWatchedAt: String? = null,
    @SerialName("show") val show: SimklShowEntry,
    @SerialName("seasons") val seasons: List<SimklWatchedSeason> = emptyList(),
)

@Serializable
public data class SimklShowEntry(
    @SerialName("title") val title: String? = null,
    @SerialName("year") val year: Int? = null,
    @SerialName("ids") val ids: SimklShowIds,
)

@Serializable
public data class SimklShowIds(
    @SerialName("simkl") val simkl: Long? = null,
    @SerialName("tmdb") val tmdb: String? = null,
    @SerialName("imdb") val imdb: String? = null,
    @SerialName("tvdb") val tvdb: String? = null,
)

@Serializable
public data class SimklWatchedSeason(
    @SerialName("number") val number: Int,
    @SerialName("episodes") val episodes: List<SimklWatchedEpisode> = emptyList(),
)

@Serializable
public data class SimklWatchedEpisode(
    @SerialName("number") val number: Int,
    @SerialName("watched_at") val watchedAt: String? = null,
)
