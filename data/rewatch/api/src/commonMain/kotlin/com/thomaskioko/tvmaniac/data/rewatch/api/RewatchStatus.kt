package com.thomaskioko.tvmaniac.data.rewatch.api

public data class RewatchStatus(
    val finishedCount: Int = 0,
    val openSession: OpenRewatchSession? = null,
)

public data class OpenRewatchSession(
    val id: Long,
    val watchedEpisodes: Int,
    val airedEpisodes: Int,
)
