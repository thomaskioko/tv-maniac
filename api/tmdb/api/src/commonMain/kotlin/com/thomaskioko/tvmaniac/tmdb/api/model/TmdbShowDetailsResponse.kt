package com.thomaskioko.tvmaniac.tmdb.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TmdbShowDetailsResponse(
    @SerialName("adult") var adult: Boolean,
    @SerialName("backdrop_path") var backdropPath: String? = null,
    @SerialName("episode_run_time") var episodeRunTime: ArrayList<Int> = arrayListOf(),
    @SerialName("first_air_date") var firstAirDate: String? = null,
    @SerialName("genres") var genres: ArrayList<GenreResponse>,
    @SerialName("id") var id: Int,
    @SerialName("last_air_date") var lastAirDate: String? = null,
    @SerialName("last_episode_to_air") var lastEpisodeToAir: LastEpisodeToAirResponse? = null,
    @SerialName("name") var name: String,
    @SerialName("next_episode_to_air") var nextEpisodeToAir: NextEpisodeToAirResponse? = null,
    @SerialName("networks") var networks: ArrayList<NetworksResponse>,
    @SerialName("number_of_episodes") var numberOfEpisodes: Int,
    @SerialName("number_of_seasons") var numberOfSeasons: Int,
    @SerialName("overview") var overview: String,
    @SerialName("popularity") var popularity: Double,
    @SerialName("poster_path") var posterPath: String? = null,
    @SerialName("seasons") var seasons: ArrayList<SeasonsResponse>,
    @SerialName("status") var status: String,
    @SerialName("vote_average") var voteAverage: Double,
    @SerialName("vote_count") var voteCount: Int,
    @SerialName("videos") var videos: VideosResponse,
    @SerialName("credits") var credits: CreditsResponse,
    @SerialName("original_language") var originalLanguage: String? = null,
)
