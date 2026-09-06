package com.thomaskioko.tvmaniac.lists.api

public data class ListShowItem(
    val tmdbId: Long,
    val name: String,
    val posterPath: String?,
    val year: String?,
)
