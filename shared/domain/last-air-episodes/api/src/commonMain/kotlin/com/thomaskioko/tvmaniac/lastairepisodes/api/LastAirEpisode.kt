package com.thomaskioko.tvmaniac.lastairepisodes.api

data class LastAirEpisode(
    val id: Long,
    val name: String?,
    val overview: String,
    val airDate: String,
    val episodeNumber: Long,
    val seasonNumber: Long,
    val posterPath: String?,
    val voteAverage: Double?,
    val voteCount: Long?,
    val title: String
)
