package com.thomaskioko.tvmaniac.presentation.watchlist.model

data class LibraryItem(
    val traktId: Long = 0,
    val tmdbId: Long? = 0,
    val title: String,
    val posterImageUrl: String? = null,
)
