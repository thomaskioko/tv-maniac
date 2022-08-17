package com.thomaskioko.tvmaniac.remote.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LastEpisodeToAir(
    @SerialName("id") val id: Int?,
    @SerialName("name") val name: String?,
    @SerialName("overview") val overview: String?,
    @SerialName("air_date") val airDate: String?,
    @SerialName("episode_number") val episodeNumber: Int?,
    @SerialName("season_number") val seasonNumber: Int?,
    @SerialName("still_path") val stillPath: String?,
    @SerialName("vote_average") val voteAverage: Double?,
    @SerialName("vote_count") val voteCount: Int?,
    @SerialName("production_code") val productionCode: String?
)
