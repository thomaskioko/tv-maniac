package com.thomaskioko.tvmaniac.followedshows.api

import kotlin.time.Instant

public data class FollowedShowEntry(
    val id: Long = 0,
    val tmdbId: Long,
    val followedAt: Instant,
    val pendingAction: PendingAction = PendingAction.NOTHING,
    val traktId: Long? = null,
)
