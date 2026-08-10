package com.thomaskioko.tvmaniac.data.rewatch.api

public data class RewatchSession(
    val id: Long,
    val showId: Long,
    val startedAt: Long,
    val closedAt: Long? = null,
    val providerSessionId: Long? = null,
)
