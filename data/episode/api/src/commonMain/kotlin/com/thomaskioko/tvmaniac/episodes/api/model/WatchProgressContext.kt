package com.thomaskioko.tvmaniac.episodes.api.model

public data class WatchProgressContext(
    val showId: Long,
    val totalEpisodes: Int,
    val watchedEpisodes: Int,
    val lastWatchedSeasonNumber: Int?,
    val lastWatchedEpisodeNumber: Int?,
    val isWatchingOutOfOrder: Boolean,
    val hasUnwatchedEarlierEpisodes: Boolean,
    val progressPercentage: Float,
)
