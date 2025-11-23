package com.thomaskioko.tvmaniac.domain.user.model

import com.thomaskioko.tvmaniac.data.user.api.model.UserWatchTime

public data class UserStats(
    val showsWatched: Int,
    val episodesWatched: Int,
    val userWatchTime: UserWatchTime,
)
