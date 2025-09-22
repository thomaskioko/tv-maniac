package com.thomaskioko.tvmaniac.episodes.api.model

public data class LastWatchedEpisode(
    val showId: Long,
    val episodeId: Long,
    val seasonNumber: Int,
    val episodeNumber: Int,
    val watchedAt: Long,
    val absoluteEpisodeNumber: Long = (seasonNumber * 1000L) + episodeNumber,
)
