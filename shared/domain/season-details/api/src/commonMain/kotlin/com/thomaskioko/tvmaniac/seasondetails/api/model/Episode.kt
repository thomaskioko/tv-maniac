package com.thomaskioko.tvmaniac.seasondetails.api.model

data class Episode(
    val id: Int,
    val seasonId: Int,
    val episodeTitle: String,
    val episodeNumberTitle: String,
    val overview: String,
    val imageUrl: String?,
    val runtime: Int,
    val voteCount: Int,
    val episodeNumber: String,
    val seasonEpisodeNumber: String,
)
