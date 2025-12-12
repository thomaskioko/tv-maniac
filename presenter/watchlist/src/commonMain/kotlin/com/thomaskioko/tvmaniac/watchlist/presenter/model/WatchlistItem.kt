package com.thomaskioko.tvmaniac.watchlist.presenter.model

data class WatchlistItem(
    val tmdbId: Long,
    val title: String,
    val posterImageUrl: String? = null,
    val watchProgress: Float = 0F,
    val watchedCount: Long = 0,
    val totalEpisodeCount: Long = 0,
    val seasonCount: Long = 0,
    val episodeCount: Long = 0,
    val status: String? = null,
    val year: String? = null,
)
