package com.thomaskioko.tvmaniac.data.rewatch.api

public data class RemoteRewatchSession(
    val providerSessionId: Long?,
    val lastWatchedAt: Long?,
    val status: RemoteRewatchSessionStatus = RemoteRewatchSessionStatus.ACTIVE,
    val startedAt: Long? = null,
    val episodes: List<RemoteRewatchEpisode> = emptyList(),
)
