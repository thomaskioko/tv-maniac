package com.thomaskioko.tvmaniac.data.ratings.api

import com.thomaskioko.tvmaniac.followedshows.api.PendingAction

public data class EpisodeRatingEntry(
    val episodeId: Long,
    val userRating: Long? = null,
    val ratedAt: Long? = null,
    val pendingAction: PendingAction = PendingAction.NOTHING,
)
