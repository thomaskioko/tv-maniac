package com.thomaskioko.tvmaniac.data.rewatch.api

public data class UnsentRewatchEpisode(
    val rowId: Long,
    val sessionId: Long,
    val showId: Long,
    val seasonNumber: Long,
    val episodeNumber: Long,
    val watchedAt: Long,
)
