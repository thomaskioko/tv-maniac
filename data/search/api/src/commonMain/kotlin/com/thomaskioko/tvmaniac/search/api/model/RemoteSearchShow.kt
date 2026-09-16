package com.thomaskioko.tvmaniac.search.api.model

public data class RemoteSearchShow(
    val providerShowId: String,
    val tmdbId: Long?,
    val title: String,
    val year: Int?,
    val overview: String?,
    val status: String?,
    val episodeCount: Int?,
    val rating: Double?,
    val votes: Long?,
    val genres: List<String>?,
    val language: String?,
    val score: Double?,
)
