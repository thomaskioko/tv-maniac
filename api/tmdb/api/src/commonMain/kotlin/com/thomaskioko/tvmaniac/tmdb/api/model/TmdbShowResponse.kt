package com.thomaskioko.tvmaniac.tmdb.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TmdbShowResult(
    @SerialName("page") var page: Int,
    @SerialName("total_pages") var totalPages: Int,
    @SerialName("total_results") var totalResults: Int,
    @SerialName("results") var results: ArrayList<TmdbShowResponse> = arrayListOf(),
)

@Serializable
public data class TmdbShowResponse(
    @SerialName("id") var id: Int,
    @SerialName("name") var name: String,
    @SerialName("overview") var overview: String,
    @SerialName("popularity") var popularity: Double,
    @SerialName("vote_average") var voteAverage: Double,
    @SerialName("vote_count") var voteCount: Int,
    @SerialName("genre_ids") var genreIds: ArrayList<Int>,
    @SerialName("origin_country") var originCountry: ArrayList<String>,
    @SerialName("backdrop_path") var backdropPath: String? = null,
    @SerialName("first_air_date") var firstAirDate: String? = null,
    @SerialName("original_language") var originalLanguage: String? = null,
    @SerialName("original_name") var originalName: String? = null,
    @SerialName("poster_path") var posterPath: String? = null,
)
