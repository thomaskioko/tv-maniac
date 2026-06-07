package com.thomaskioko.tvmaniac.data.library.model

import kotlin.time.Instant

public data class RemoteFollowedShow(
    val showId: Long,
    val tmdbId: Long,
    val title: String,
    val year: Int?,
    val followedAt: Instant,
)
