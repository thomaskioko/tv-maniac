package com.thomaskioko.tvmaniac.trakt.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TraktSeasonsResponse(
    @SerialName("number") val number: Int,
    @SerialName("ids") val ids: SeasonIds,
    @SerialName("votes") val votes: Int,
    @SerialName("rating") val rating: Double,
    @SerialName("aired_episodes") val airedEpisodes: Int,
    @SerialName("episode_count") val episodeCount: Int,
    @SerialName("title") val title: String,
    @SerialName("overview") val overview: String?,
    @SerialName("first_aired") val firstAirDate: String?,
)

@Serializable
public data class SeasonIds(
    @SerialName("trakt") val trakt: Int,
    @SerialName("tmdb") val tmdb: Int?,
)
