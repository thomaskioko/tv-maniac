package com.thomaskioko.tvmaniac.presentation.moreshows

data class TvShow(
    val traktId: Long = 0,
    val tmdbId: Long? = 0,
    val title: String = "",
    val posterImageUrl: String? = null,
    val backdropImageUrl: String? = null,
    val isFollowed: Boolean = false,
)
