package com.thomaskioko.tvmaniac.domain.following

data class FollowedShow(
    val traktId: Long = 0,
    val tmdbId: Long? = 0,
    val title: String,
    val posterImageUrl: String? = null,
)
