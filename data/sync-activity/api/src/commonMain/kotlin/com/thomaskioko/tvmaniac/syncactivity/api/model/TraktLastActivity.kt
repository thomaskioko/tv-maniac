package com.thomaskioko.tvmaniac.syncactivity.api.model

import kotlin.time.Instant

public data class TraktLastActivity(
    val id: Long,
    val activityType: ActivityType,
    val remoteTimestamp: Instant,
    val syncedRemoteTimestamp: Instant?,
    val fetchedAt: Instant,
)
