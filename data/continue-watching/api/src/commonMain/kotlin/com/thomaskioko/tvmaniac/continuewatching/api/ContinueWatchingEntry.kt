package com.thomaskioko.tvmaniac.continuewatching.api

public data class ContinueWatchingEntry(
    val traktId: Long,
    val tmdbId: Long?,
    val airedEpisodes: Long,
    val completedCount: Long,
    val lastWatchedAt: Long,
    val lastUpdatedAt: Long,
)
