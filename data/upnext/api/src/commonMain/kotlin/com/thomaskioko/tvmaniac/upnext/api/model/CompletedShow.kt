package com.thomaskioko.tvmaniac.upnext.api.model

public data class CompletedShow(
    val showId: Long,
    val showTmdbId: Long?,
    val showName: String?,
    val showPoster: String?,
    val lastWatchedAt: Long,
    val watchedCount: Long,
    val totalCount: Long,
)
