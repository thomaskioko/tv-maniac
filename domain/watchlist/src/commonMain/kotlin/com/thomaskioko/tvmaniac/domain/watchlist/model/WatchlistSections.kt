package com.thomaskioko.tvmaniac.domain.watchlist.model

data class WatchlistSections(
    val watchNext: List<WatchlistShowInfo>,
    val stale: List<WatchlistShowInfo>,
)

data class WatchlistShowInfo(
    val tmdbId: Long,
    val title: String,
    val posterImageUrl: String?,
    val status: String?,
    val year: String?,
    val seasonCount: Long,
    val episodeCount: Long,
    val episodesWatched: Long,
    val totalEpisodesTracked: Long,
    val watchProgress: Float,
    val lastWatchedAt: Long?,
)
