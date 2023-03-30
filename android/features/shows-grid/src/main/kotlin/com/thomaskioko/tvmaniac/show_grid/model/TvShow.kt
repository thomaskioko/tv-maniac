package com.thomaskioko.tvmaniac.show_grid.model

data class TvShow(
    val traktId: Long = 0,
    val tmdbId: Long? = 0,
    val title: String = "",
    val posterImageUrl: String? = null,
    val backdropImageUrl: String? = null,
)