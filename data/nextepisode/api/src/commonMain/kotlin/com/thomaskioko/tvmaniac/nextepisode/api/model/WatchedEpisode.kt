package com.thomaskioko.tvmaniac.nextepisode.api.model

public data class WatchedEpisode(
    val id: Long,
    val showId: Long,
    val episodeId: Long,
    val seasonNumber: Long,
    val episodeNumber: Long,
    val watchedAt: Long,
    val watchProgress: Float,
)
