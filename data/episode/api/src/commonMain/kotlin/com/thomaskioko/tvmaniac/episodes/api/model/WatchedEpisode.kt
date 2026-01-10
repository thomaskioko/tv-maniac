package com.thomaskioko.tvmaniac.episodes.api.model

public data class WatchedEpisode(
    val id: Long,
    val showTraktId: Long,
    val episodeId: Long,
    val seasonNumber: Long,
    val episodeNumber: Long,
    val watchedAt: Long,
    val watchProgress: Float,
)
