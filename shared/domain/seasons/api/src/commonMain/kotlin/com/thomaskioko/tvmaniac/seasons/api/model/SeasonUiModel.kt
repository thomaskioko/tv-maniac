package com.thomaskioko.tvmaniac.seasons.api.model

data class SeasonUiModel(
    val seasonId: Int,
    val tvShowId: Int,
    val name: String,
    val overview: String,
    val seasonNumber: Int,
    val episodeCount: Int
)
