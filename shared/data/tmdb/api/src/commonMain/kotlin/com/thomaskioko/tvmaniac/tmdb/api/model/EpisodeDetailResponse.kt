package com.thomaskioko.tvmaniac.tmdb.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EpisodeDetailResponse(
    @SerialName("air_date") val air_date: String,
    @SerialName("episode_number") val episode_number: Int,
    @SerialName("name") val name: String,
    @SerialName("overview") val overview: String,
    @SerialName("id") val id: Int,
    @SerialName("production_code") val production_code: String,
    @SerialName("season_number") val season_number: Int,
    @SerialName("still_path") val still_path: String?,
    @SerialName("vote_average") val vote_average: Double,
    @SerialName("vote_count") val vote_count: Int
)
