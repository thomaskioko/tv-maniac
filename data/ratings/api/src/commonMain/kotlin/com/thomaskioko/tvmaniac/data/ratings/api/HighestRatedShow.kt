package com.thomaskioko.tvmaniac.data.ratings.api

public data class HighestRatedShow(
    val showId: Long,
    val title: String,
    val posterPath: String?,
    val userRating: Long,
)
