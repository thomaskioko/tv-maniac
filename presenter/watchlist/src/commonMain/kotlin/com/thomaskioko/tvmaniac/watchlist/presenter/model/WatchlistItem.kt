package com.thomaskioko.tvmaniac.watchlist.presenter.model

public data class WatchlistItem(
    val tmdbId: Long,
    val title: String,
    val posterImageUrl: String? = null,
    val status: String? = null,
    val year: String? = null,
    val seasonCount: Long = 0,
    val episodeCount: Long = 0,
    val episodesWatched: Long = 0,
    val totalEpisodesTracked: Long = 0,
    val watchProgress: Float = 0F,
    val lastWatchedAt: Long? = null,
)
