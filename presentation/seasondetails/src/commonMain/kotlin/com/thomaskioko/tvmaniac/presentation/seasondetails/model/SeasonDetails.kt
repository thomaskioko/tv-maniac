package com.thomaskioko.tvmaniac.presentation.seasondetails.model

import kotlinx.collections.immutable.ImmutableList

data class SeasonDetails(
    val seasonId: Long,
    val seasonName: String,
    val episodeCount: Long,
    val watchProgress: Float,
    val episodes: ImmutableList<Episode>,
)
