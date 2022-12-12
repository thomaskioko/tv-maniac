package com.thomaskioko.tvmaniac.domain.following.api


sealed interface FollowingState

object LoadingShows: FollowingState

data class FollowingContent(
    val list: List<FollowedShow> = emptyList()
): FollowingState

data class ErrorLoadingShows(val message: String): FollowingState