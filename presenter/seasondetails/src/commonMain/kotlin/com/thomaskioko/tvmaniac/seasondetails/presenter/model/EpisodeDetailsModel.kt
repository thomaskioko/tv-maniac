package com.thomaskioko.tvmaniac.seasondetails.presenter.model

data class EpisodeDetailsModel(
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
