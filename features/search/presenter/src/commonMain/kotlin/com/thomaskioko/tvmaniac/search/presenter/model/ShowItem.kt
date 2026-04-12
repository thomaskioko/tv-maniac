package com.thomaskioko.tvmaniac.search.presenter.model

public data class ShowItem(
    val tmdbId: Long = 0,
    val traktId: Long = 0,
    val title: String = "",
    val status: String? = null,
    val voteAverage: Double? = null,
    val year: String? = null,
    val posterImageUrl: String? = null,
    val overview: String? = null,
    val inLibrary: Boolean = false,
)
