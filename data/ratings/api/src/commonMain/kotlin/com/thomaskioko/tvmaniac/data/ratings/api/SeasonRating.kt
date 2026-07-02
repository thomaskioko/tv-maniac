package com.thomaskioko.tvmaniac.data.ratings.api

import com.thomaskioko.tvmaniac.followedshows.api.PendingAction

public data class SeasonRating(
    val userRating: Int?,
    val pendingAction: PendingAction,
)
