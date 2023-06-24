package com.thomaskioko.tvmaniac.presentation.discover.model

data class TvShow(
    val traktId: Long = 0,
    val tmdbId: Long? = 0,
    val title: String = "",
    val overview: String = "",
    val language: String? = null,
    val posterImageUrl: String? = null,
    val backdropImageUrl: String? = null,
    val year: String = "",
    val status: String? = null,
    val votes: Long = 0,
    val numberOfSeasons: Long? = null,
    val numberOfEpisodes: Long? = null,
    val rating: Double = 0.0,
    val genres: List<String> = listOf(),
    val isFollowed: Boolean = false,
)
