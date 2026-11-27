package com.thomaskioko.tvmaniac.data.rewatch.api

public data class RewatchRestore(
    val sessionId: Long,
    val seasonNumber: Long,
    val episodeNumber: Long,
    val watchedAt: Long,
    val ordinal: Long,
)
