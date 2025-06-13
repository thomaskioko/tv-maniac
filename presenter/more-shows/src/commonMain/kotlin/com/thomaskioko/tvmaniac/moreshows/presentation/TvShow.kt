package com.thomaskioko.tvmaniac.moreshows.presentation

data class TvShow(
    val tmdbId: Long = 0,
    val title: String = "",
    val posterImageUrl: String? = null,
    val inLibrary: Boolean = false,
)
