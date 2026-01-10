package com.thomaskioko.tvmaniac.domain.watchlist.model

public data class WatchlistSections(
    val watchNext: List<WatchlistShowInfo>,
    val stale: List<WatchlistShowInfo>,
)

public data class WatchlistShowInfo(
    val traktId: Long,
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
    val nextEpisode: NextEpisodeInfo? = null,
)

public data class NextEpisodeInfo(
    val episodeId: Long,
    val episodeTitle: String,
    val seasonNumber: Long,
    val episodeNumber: Long,
    val stillPath: String?,
    val airDate: String?,
)
