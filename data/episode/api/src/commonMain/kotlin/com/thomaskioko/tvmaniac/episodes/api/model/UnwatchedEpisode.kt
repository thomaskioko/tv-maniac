package com.thomaskioko.tvmaniac.episodes.api.model

public data class UnwatchedEpisode(
    val episodeId: Long,
    val seasonNumber: Long,
    val episodeNumber: Long,
    val seasonId: Long,
)
