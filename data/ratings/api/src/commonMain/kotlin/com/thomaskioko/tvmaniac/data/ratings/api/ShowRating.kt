package com.thomaskioko.tvmaniac.data.ratings.api

import com.thomaskioko.tvmaniac.followedshows.api.PendingAction

public data class ShowRating(
    val userRating: Int?,
    val communityRating: Double?,
    val communityVotes: Long?,
    val pendingAction: PendingAction,
)
