package com.thomaskioko.tvmaniac.episodes.api.model

public data class LastWatchedEpisode(
    val showTraktId: Long,
    val episodeId: Long,
    val seasonNumber: Long,
    val episodeNumber: Long,
    val absoluteEpisodeNumber: Long = (seasonNumber * 1000L) + episodeNumber,
)
