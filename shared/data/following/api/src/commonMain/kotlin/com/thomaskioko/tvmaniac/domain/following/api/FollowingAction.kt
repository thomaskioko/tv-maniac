package com.thomaskioko.tvmaniac.domain.following.api

sealed interface FollowingAction

object ReloadFollowedShows : FollowingAction