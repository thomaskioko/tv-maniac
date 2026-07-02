package com.thomaskioko.tvmaniac.data.ratings.api

import com.thomaskioko.tvmaniac.followedshows.api.PendingAction

public data class EpisodeRating(
    val userRating: Int?,
    val pendingAction: PendingAction,
)
