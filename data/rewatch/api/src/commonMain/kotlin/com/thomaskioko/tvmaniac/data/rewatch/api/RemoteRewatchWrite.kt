package com.thomaskioko.tvmaniac.data.rewatch.api

public data class RemoteRewatchWrite(
    val localSessionId: Long,
    val providerShowId: Long,
    val providerSessionId: Long?,
    val seasonNumber: Long,
    val episodeNumber: Long,
    val watchedAt: Long,
)

public data class RemoteRewatchWriteResult(
    val providerSessionId: Long? = null,
    val skipped: Boolean = false,
)
