package com.thomaskioko.tvmaniac.remote.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SeasonResponse(
    @SerialName("air_date") val air_date: String,
    @SerialName("episodes") val episodes: List<EpisodesResponse>,
    @SerialName("name") val name: String,
    @SerialName("overview") val overview: String,
    @SerialName("id") val id: Int,
    @SerialName("poster_path") val poster_path: String,
    @SerialName("season_number") val season_number: Int
)
