package com.thomaskioko.tvmaniac.trakt.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TraktSeasonEpisodesResponse(
    @SerialName("number") val number: Int,
    @SerialName("ids") val ids: SeasonIds,
    @SerialName("votes") val votes: Int,
    @SerialName("rating") val rating: Double,
    @SerialName("aired_episodes") val airedEpisodes: Int,
    @SerialName("episode_count") val episodeCount: Int,
    @SerialName("title") val title: String,
    @SerialName("overview") val overview: String?,
    @SerialName("first_aired") val firstAirDate: String?,
    @SerialName("episodes") val episodes: List<TraktEpisodesResponse>,
)

@Serializable
public data class TraktEpisodesResponse(
    @SerialName("season") val seasonNumber: Int,
    @SerialName("number") val episodeNumber: Int,
    @SerialName("ids") val ids: EpisodeIds,
    @SerialName("title") val title: String,
    @SerialName("overview") val overview: String?,
    @SerialName("updated_at") val updatedAt: String,
    @SerialName("rating") val ratings: Double,
    @SerialName("votes") val votes: Int,
    @SerialName("runtime") val runtime: Int,
)

@Serializable
public data class EpisodeIds(
    @SerialName("trakt") val trakt: Int,
    @SerialName("tmdb") val tmdb: Int?,
)
