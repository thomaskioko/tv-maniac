package com.thomaskioko.tvmaniac.data.seasondetails.model

data class Episode(
    val id: Long,
    val seasonId: Long,
    val episodeTitle: String,
    val episodeNumberTitle: String,
    val overview: String,
    val imageUrl: String?,
    val runtime: Long,
    val voteCount: Long,
    val episodeNumber: String,
    val seasonEpisodeNumber: String,
)
