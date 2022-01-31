package com.thomaskioko.tvmaniac.seasonepisodes.api.model

data class SeasonWithEpisodes(
    val seasonName: String,
    val episodeCount: Int,
    val watchProgress: Float,
    val episodes: List<Episode>
)
