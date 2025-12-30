package com.thomaskioko.tvmaniac.tmdb.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class NextEpisodeToAirResponse(
    @SerialName("id") var id: Int,
    @SerialName("name") var name: String,
    @SerialName("overview") var overview: String,
    @SerialName("vote_average") var voteAverage: Double,
    @SerialName("vote_count") var voteCount: Int,
    @SerialName("air_date") var airDate: String,
    @SerialName("episode_number") var episodeNumber: Int,
    @SerialName("episode_type") var episodeType: String,
    @SerialName("runtime") var runtime: Int? = null,
    @SerialName("season_number") var seasonNumber: Int,
    @SerialName("show_id") var showId: Int,
    @SerialName("still_path") var stillPath: String? = null,
)
