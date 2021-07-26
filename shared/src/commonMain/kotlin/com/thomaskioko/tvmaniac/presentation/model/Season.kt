package com.thomaskioko.tvmaniac.presentation.model

import kotlinx.serialization.Serializable

@Serializable
data class Season(
    val seasonId: Int,
    val tvShowId: Int,
    val name: String,
    val overview: String,
    val seasonNumber: Int,
    val episodeCount: Int,
    val episodeList: List<Episode> = emptyList()
)

