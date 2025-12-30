package com.thomaskioko.tvmaniac.discover.presenter.model

public data class DiscoverShow(
    val tmdbId: Long = 0,
    val title: String = "",
    val posterImageUrl: String? = null,
    val inLibrary: Boolean = false,
    val overView: String? = "",
)
