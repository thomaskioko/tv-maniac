package com.thomaskioko.tvmaniac.followedshows.api

import kotlin.time.Instant

public data class FollowedShowEntry(
    val id: Long = 0,
    val traktId: Long,
    val tmdbId: Long? = null,
    val followedAt: Instant,
    val pendingAction: PendingAction = PendingAction.NOTHING,
)
