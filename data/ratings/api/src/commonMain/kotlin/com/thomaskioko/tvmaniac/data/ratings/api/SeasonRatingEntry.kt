package com.thomaskioko.tvmaniac.data.ratings.api

import com.thomaskioko.tvmaniac.followedshows.api.PendingAction

public data class SeasonRatingEntry(
    val seasonId: Long,
    val userRating: Long? = null,
    val ratedAt: Long? = null,
    val pendingAction: PendingAction = PendingAction.NOTHING,
)
