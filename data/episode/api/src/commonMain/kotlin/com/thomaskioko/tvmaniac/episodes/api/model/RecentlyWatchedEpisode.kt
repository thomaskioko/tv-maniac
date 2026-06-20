package com.thomaskioko.tvmaniac.episodes.api.model

public data class RecentlyWatchedEpisode(
    val showId: Long,
    val showTitle: String,
    val posterPath: String?,
    val seasonNumber: Long,
    val episodeNumber: Long,
    val episodeTitle: String?,
    val watchedAt: Long,
)
