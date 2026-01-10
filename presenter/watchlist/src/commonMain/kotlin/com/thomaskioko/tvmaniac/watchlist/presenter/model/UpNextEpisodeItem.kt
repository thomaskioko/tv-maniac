package com.thomaskioko.tvmaniac.watchlist.presenter.model

public data class UpNextEpisodeItem(
    val showTraktId: Long,
    val showName: String,
    val showPoster: String?,
    val episodeId: Long,
    val episodeTitle: String,
    val episodeNumberFormatted: String,
    val seasonId: Long,
    val seasonNumber: Long,
    val episodeNumber: Long,
    val runtime: String?,
    val stillImage: String?,
    val overview: String,
    val badge: EpisodeBadge = EpisodeBadge.NONE,
    val remainingEpisodes: Int = 0,
    val lastWatchedAt: Long? = null,
)
