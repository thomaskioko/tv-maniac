package com.thomaskioko.tvmaniac.details.api.model

data class Season(
    val seasonId: Int,
    val tvShowId: Int,
    val name: String,
    val overview: String?,
    val seasonNumber: Int,
    val episodeCount: Int
)
