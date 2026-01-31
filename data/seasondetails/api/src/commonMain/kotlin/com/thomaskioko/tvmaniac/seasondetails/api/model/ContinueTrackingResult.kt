package com.thomaskioko.tvmaniac.seasondetails.api.model

import kotlinx.collections.immutable.ImmutableList

public data class ContinueTrackingResult(
    val episodes: ImmutableList<EpisodeDetails>,
    val currentSeasonNumber: Long,
    val currentSeasonId: Long,
)
