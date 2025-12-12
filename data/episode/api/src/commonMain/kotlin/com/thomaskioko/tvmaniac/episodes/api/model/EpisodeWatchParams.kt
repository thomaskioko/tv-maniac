package com.thomaskioko.tvmaniac.episodes.api.model

public data class EpisodeWatchParams(
    val episodeId: Long,
    val seasonNumber: Long,
    val episodeNumber: Long,
    val watchedAt: Long? = null,
)
