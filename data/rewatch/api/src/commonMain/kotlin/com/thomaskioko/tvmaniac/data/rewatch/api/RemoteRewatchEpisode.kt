package com.thomaskioko.tvmaniac.data.rewatch.api

public data class RemoteRewatchEpisode(
    val seasonNumber: Long,
    val episodeNumber: Long,
    val watchedAt: Long?,
    val viewings: Long = 1,
)
