package com.thomaskioko.tvmaniac.trakt.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TraktSeasonEpisodesResponse(
    @SerialName("number") val number: Int,
    @SerialName("ids") val ids: SeasonIds,
    @SerialName("votes") val votes: Int = 0,
    @SerialName("rating") val rating: Double = 0.0,
    @SerialName("aired_episodes") val airedEpisodes: Int = 0,
    @SerialName("episode_count") val episodeCount: Int = 0,
    @SerialName("title") val title: String? = null,
    @SerialName("overview") val overview: String? = null,
    @SerialName("first_aired") val firstAirDate: String? = null,
    @SerialName("episodes") val episodes: List<TraktEpisodesResponse> = emptyList(),
)

@Serializable
public data class TraktEpisodesResponse(
    @SerialName("season") val seasonNumber: Int,
    @SerialName("number") val episodeNumber: Int,
    @SerialName("ids") val ids: EpisodeIds,
    @SerialName("title") val title: String = "",
    @SerialName("overview") val overview: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("rating") val ratings: Double? = null,
    @SerialName("votes") val votes: Int? = null,
    @SerialName("runtime") val runtime: Int? = null,
    @SerialName("first_aired") val firstAired: String? = null,
)

@Serializable
public data class EpisodeIds(
    @SerialName("trakt") val trakt: Int,
    @SerialName("tmdb") val tmdb: Int?,
)
