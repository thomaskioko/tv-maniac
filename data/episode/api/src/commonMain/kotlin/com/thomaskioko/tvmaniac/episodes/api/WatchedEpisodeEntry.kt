package com.thomaskioko.tvmaniac.episodes.api

import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import kotlin.time.Instant

public data class WatchedEpisodeEntry(
    val id: Long = 0,
    val showId: Long,
    val episodeId: Long?,
    val seasonNumber: Long,
    val episodeNumber: Long,
    val watchedAt: Instant,
    val pendingAction: PendingAction = PendingAction.NOTHING,
    val traktId: Long? = null,
)
