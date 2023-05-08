package com.thomaskioko.tvmaniac.presentation.following

sealed interface FollowingState

object LoadingShows : FollowingState

data class FollowingContent(
    val list: List<FollowingShow> = emptyList(),
) : FollowingState

data class ErrorLoadingShows(val message: String) : FollowingState
