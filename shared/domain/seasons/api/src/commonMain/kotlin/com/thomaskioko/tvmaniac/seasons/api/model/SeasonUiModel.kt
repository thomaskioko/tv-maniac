package com.thomaskioko.tvmaniac.seasons.api.model

import com.thomaskioko.tvmaniac.seasons.api.model.EpisodeUiModel

data class SeasonUiModel(
    val seasonId: Int,
    val tvShowId: Int,
    val name: String,
    val overview: String,
    val seasonNumber: Int,
    val episodeCount: Int,
    val episodeList: List<EpisodeUiModel> = emptyList()
)
