package com.thomaskioko.tvmaniac.seasons.api.model

data class SeasonUiModel(
    val seasonId: Long,
    val tvShowId: Long,
    val name: String,
    val overview: String,
    val seasonNumber: Long,
    val episodeCount: Int
)
