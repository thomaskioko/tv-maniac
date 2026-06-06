package com.thomaskioko.tvmaniac.data.library.model

import kotlin.time.Instant

/**
 * Provider-neutral watchlist entry: a followed show with the timestamp it was added.
 *
 * [showId] is the active provider's show identifier (used as the app's show id); [tmdbId] resolves
 * artwork and cross-provider identity.
 */
public data class RemoteFollowedShow(
    val showId: Long,
    val tmdbId: Long,
    val title: String,
    val year: Int?,
    val followedAt: Instant,
)
