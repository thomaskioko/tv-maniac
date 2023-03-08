package com.thomaskioko.tvmaniac.domain.following

sealed interface FollowingAction

object ReloadFollowedShows : FollowingAction