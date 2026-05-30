package com.thomaskioko.tvmaniac.favorites.api

public data class FavoriteShow(
    val traktId: Long,
    val tmdbId: Long,
    val title: String,
    val posterPath: String?,
    val year: String?,
)
