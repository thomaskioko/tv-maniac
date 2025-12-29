package com.thomaskioko.tvmaniac.seasondetails.presenter.model

public data class EpisodeDetailsModel(
    val id: Long,
    val seasonId: Long,
    val episodeTitle: String,
    val episodeNumberTitle: String,
    val overview: String,
    val imageUrl: String?,
    val runtime: Long,
    val voteCount: Long,
    val episodeNumber: Long,
    val seasonNumber: Long,
    val seasonEpisodeNumber: String,
    val isWatched: Boolean,
    val daysUntilAir: Int?,
    val hasPreviousUnwatched: Boolean,
    val isEpisodeUpdating: Boolean = false,
)
