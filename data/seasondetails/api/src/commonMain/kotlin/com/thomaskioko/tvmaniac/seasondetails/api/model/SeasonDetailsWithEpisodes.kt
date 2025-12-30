package com.thomaskioko.tvmaniac.seasondetails.api.model

public data class SeasonDetailsWithEpisodes(
    val seasonId: Long,
    val tvShowId: Long,
    val name: String,
    val showTitle: String,
    val seasonOverview: String,
    val imageUrl: String?,
    val seasonNumber: Long,
    val episodeCount: Long,
    val episodes: List<EpisodeDetails>,
)
