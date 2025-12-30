package com.thomaskioko.tvmaniac.tmdb.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TmdbSeasonDetailsResponse(
    @SerialName("air_date") var airDate: String? = null,
    @SerialName("episodes") var episodes: ArrayList<EpisodesResponse>,
    @SerialName("name") var name: String,
    @SerialName("overview") var overview: String,
    @SerialName("id") var id: Int,
    @SerialName("poster_path") var posterPath: String? = null,
    @SerialName("season_number") var seasonNumber: Int,
    @SerialName("vote_average") var voteAverage: Double,
    @SerialName("videos") var videos: VideosResponse,
    @SerialName("images") var images: ImagesResponse,
    @SerialName("credits") var credits: CreditsResponse,
)
