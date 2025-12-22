package com.thomaskioko.tvmaniac.domain.watchlist.model

data class UpNextSections(
    val watchNext: List<UpNextEpisodeInfo>,
    val stale: List<UpNextEpisodeInfo>,
)

data class UpNextEpisodeInfo(
    val showId: Long,
    val showName: String,
    val showPoster: String?,
    val episodeId: Long,
    val episodeTitle: String?,
    val seasonId: Long,
    val seasonNumber: Long,
    val episodeNumber: Long,
    val runtime: Long?,
    val stillImage: String?,
    val overview: String?,
    val airDate: String?,
    val remainingEpisodes: Int,
    val lastWatchedAt: Long?,
)
