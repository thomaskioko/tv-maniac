package com.thomaskioko.tvmaniac.domain.following.api

data class FollowedShow(
    val traktId: Int = 0,
    val tmdbId: Int? = 0,
    val title: String,
    val posterImageUrl: String? = null,
)