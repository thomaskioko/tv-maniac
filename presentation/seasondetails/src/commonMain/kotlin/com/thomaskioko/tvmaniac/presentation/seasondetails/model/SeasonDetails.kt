package com.thomaskioko.tvmaniac.presentation.seasondetails.model

data class SeasonDetails(
    val seasonId: Long,
    val seasonName: String,
    val episodeCount: Long,
    val watchProgress: Float,
    val episodes: List<Episode>,
)
