package com.thomaskioko.tvmaniac.shows.api.model

public data class ShowEntity(
    val traktId: Long,
    val tmdbId: Long,
    val inLibrary: Boolean,
    val posterPath: String?,
    val overview: String? = null,
    val status: String? = null,
    val year: String? = null,
    val voteAverage: Double? = null,
    val title: String,
    val page: Long = 0,
)
