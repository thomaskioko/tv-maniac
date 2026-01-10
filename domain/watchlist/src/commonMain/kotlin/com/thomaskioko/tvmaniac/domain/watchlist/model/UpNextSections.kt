package com.thomaskioko.tvmaniac.domain.watchlist.model

public data class UpNextSections(
    val watchNext: List<UpNextEpisodeInfo>,
    val stale: List<UpNextEpisodeInfo>,
)

public data class UpNextEpisodeInfo(
    val showTraktId: Long,
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
