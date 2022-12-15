package com.thomaskioko.tvmaniac.seasondetails.api.model


data class SeasonDetails(
    val seasonId: Int,
    val seasonName: String,
    val episodeCount: Int,
    val watchProgress: Float,
    val episodes: List<Episode>
)
