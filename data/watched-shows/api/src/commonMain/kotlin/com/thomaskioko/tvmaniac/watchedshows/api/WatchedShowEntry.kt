package com.thomaskioko.tvmaniac.watchedshows.api

public data class WatchedShowEntry(
    val traktId: Long,
    val tmdbId: Long?,
    val plays: Long,
    val lastWatchedAt: Long,
    val lastUpdatedAt: Long,
)
