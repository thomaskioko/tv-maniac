package com.thomaskioko.tvmaniac.tmdb.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SeasonsResponse(
    @SerialName("air_date") var airDate: String? = null,
    @SerialName("episode_count") var episodeCount: Int,
    @SerialName("id") var id: Int,
    @SerialName("name") var name: String,
    @SerialName("overview") var overview: String,
    @SerialName("poster_path") var posterPath: String? = null,
    @SerialName("season_number") var seasonNumber: Int,
    @SerialName("vote_average") var voteAverage: Double,
)
