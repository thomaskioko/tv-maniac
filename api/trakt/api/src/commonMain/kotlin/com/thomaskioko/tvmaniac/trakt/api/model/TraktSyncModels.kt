package com.thomaskioko.tvmaniac.trakt.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TraktSyncItems(
    @SerialName("ids") val ids: List<Long>? = null,
    @SerialName("episodes") val episodes: List<TraktSyncEpisode>? = null,
    @SerialName("shows") val shows: List<TraktSyncShow>? = null,
)

@Serializable
public data class TraktSyncShow(
    @SerialName("ids") val ids: TraktShowIds,
    @SerialName("seasons") val seasons: List<TraktSyncSeason>,
)

@Serializable
public data class TraktSyncSeason(
    @SerialName("number") val number: Long,
    @SerialName("episodes") val episodes: List<TraktSyncSeasonEpisode>,
)

@Serializable
public data class TraktSyncSeasonEpisode(
    @SerialName("number") val number: Long,
    @SerialName("watched_at") val watchedAt: String? = null,
)

@Serializable
public data class TraktSyncEpisode(
    @SerialName("ids") val ids: TraktEpisodeIds,
    @SerialName("watched_at") val watchedAt: String? = null,
)

@Serializable
public data class TraktEpisodeIds(
    @SerialName("trakt") val traktId: Long? = null,
    @SerialName("tmdb") val tmdbId: Long? = null,
)

@Serializable
public data class TraktSyncResponse(
    @SerialName("added") val added: TraktSyncStats? = null,
    @SerialName("deleted") val deleted: TraktSyncStats? = null,
    @SerialName("not_found") val notFound: TraktSyncNotFound? = null,
)

@Serializable
public data class TraktSyncStats(
    @SerialName("episodes") val episodes: Int? = null,
)

@Serializable
public data class TraktSyncNotFound(
    @SerialName("episodes") val episodes: List<TraktSyncEpisode>? = null,
)

@Serializable
public data class TraktHistoryEntry(
    @SerialName("id") val id: Long,
    @SerialName("watched_at") val watchedAt: String,
    @SerialName("action") val action: String? = null,
    @SerialName("type") val type: String? = null,
    @SerialName("episode") val episode: TraktHistoryEpisode,
    @SerialName("show") val show: TraktHistoryShow,
)

@Serializable
public data class TraktHistoryEpisode(
    @SerialName("season") val season: Int,
    @SerialName("number") val number: Int,
    @SerialName("title") val title: String? = null,
    @SerialName("ids") val ids: TraktEpisodeIds,
)

@Serializable
public data class TraktHistoryShow(
    @SerialName("title") val title: String? = null,
    @SerialName("year") val year: Int? = null,
    @SerialName("ids") val ids: TraktHistoryShowIds,
)

@Serializable
public data class TraktHistoryShowIds(
    @SerialName("trakt") val traktId: Long? = null,
    @SerialName("slug") val slug: String? = null,
    @SerialName("tvdb") val tvdb: Long? = null,
    @SerialName("imdb") val imdb: String? = null,
    @SerialName("tmdb") val tmdbId: Long? = null,
)
