package com.thomaskioko.tvmaniac.episodes.api.model

import com.thomaskioko.tvmaniac.seasondetails.api.model.EpisodeDetails
import kotlinx.collections.immutable.ImmutableList

public data class ContinueTrackingResult(
    val episodes: ImmutableList<EpisodeDetails>,
    val firstUnwatchedIndex: Int,
    val currentSeasonNumber: Long,
    val currentSeasonId: Long,
)
