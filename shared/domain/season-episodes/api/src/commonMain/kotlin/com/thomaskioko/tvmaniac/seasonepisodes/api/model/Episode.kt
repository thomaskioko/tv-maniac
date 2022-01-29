package com.thomaskioko.tvmaniac.seasonepisodes.api.model

data class Episode(
    val id: Long,
    val seasonId: Long,
    val episodeTitle: String,
    val episodeNumberTitle: String,
    val overview: String,
    val imageUrl: String,
    val voteAverage: Double,
    val voteCount: Int,
    val episodeNumber: String,
    val seasonEpisodeNumber: String,
)
