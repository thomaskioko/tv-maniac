package com.thomaskioko.tvmaniac.data.showdetails.implementation

import com.thomaskioko.tvmaniac.tmdb.api.model.SeasonsResponse

internal data class ShowDetailsResponse(
    val name: String,
    val overview: String?,
    val language: String?,
    val status: String?,
    val year: String?,
    val episodeNumbers: String?,
    val seasonNumbers: String?,
    val ratings: Double,
    val voteCount: Long,
    val genres: List<String>?,
    val posterPath: String?,
    val backdropPath: String?,
    val tmdbSeasons: List<SeasonsResponse>,
    val tmdbId: Long,
    val traktId: Long?,
)
