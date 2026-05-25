package com.thomaskioko.tvmaniac.startwatching.api

public data class StartWatchingShow(
    val traktId: Long,
    val tmdbId: Long,
    val title: String,
    val posterPath: String?,
    val year: String?,
)
