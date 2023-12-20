package com.thomaskioko.tvmaniac.presentation.discover.model

data class DiscoverShow(
    val tmdbId: Long = 0,
    val title: String = "",
    val posterImageUrl: String? = null,
    val backdropImageUrl: String? = null,
    val isInLibrary: Boolean = false,
)
