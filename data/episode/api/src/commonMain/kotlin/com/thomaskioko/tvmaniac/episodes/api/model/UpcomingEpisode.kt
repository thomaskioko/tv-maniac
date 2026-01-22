package com.thomaskioko.tvmaniac.episodes.api.model

public data class UpcomingEpisode(
    val episodeId: Long,
    val seasonId: Long,
    val showId: Long,
    val episodeNumber: Long,
    val seasonNumber: Long,
    val title: String?,
    val overview: String?,
    val runtime: Long?,
    val imageUrl: String?,
    val firstAired: Long,
    val showName: String,
    val showPoster: String?,
)
