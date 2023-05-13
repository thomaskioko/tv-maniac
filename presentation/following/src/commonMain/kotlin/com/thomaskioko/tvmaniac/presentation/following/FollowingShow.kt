package com.thomaskioko.tvmaniac.presentation.following

data class FollowingShow(
    val traktId: Long = 0,
    val tmdbId: Long? = 0,
    val title: String,
    val posterImageUrl: String? = null,
)
