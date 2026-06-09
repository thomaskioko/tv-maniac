package com.thomaskioko.tvmaniac.continuewatching.api

public data class ContinueWatchingEntry(
    val showId: Long,
    val tmdbId: Long?,
    val airedEpisodes: Long,
    val completedCount: Long,
    val lastWatchedAt: Long,
    val lastUpdatedAt: Long,
    val title: String? = null,
    val year: Long? = null,
)
