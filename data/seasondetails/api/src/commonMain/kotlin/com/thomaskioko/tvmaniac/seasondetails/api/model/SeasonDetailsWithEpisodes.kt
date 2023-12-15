package com.thomaskioko.tvmaniac.seasondetails.api.model

data class SeasonDetailsWithEpisodes(
    val seasonId: Long,
    val tvShowId: Long,
    val name: String,
    val showTitle: String,
    val seasonNumber: Long,
    val episodeCount: Long,
    val episodes: List<EpisodeDetails>,
)
