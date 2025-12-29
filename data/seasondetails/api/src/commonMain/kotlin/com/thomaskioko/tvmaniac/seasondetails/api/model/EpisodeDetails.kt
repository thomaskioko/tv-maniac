package com.thomaskioko.tvmaniac.seasondetails.api.model

public data class EpisodeDetails(
    val id: Long,
    val seasonId: Long,
    val name: String,
    val seasonNumber: Long,
    val episodeNumber: Long,
    val runtime: Long,
    val overview: String,
    val voteAverage: Double,
    val voteCount: Long,
    val stillPath: String?,
    val airDate: String?,
    val isWatched: Boolean,
    val daysUntilAir: Int?,
)
