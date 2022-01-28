package com.thomaskioko.tvmaniac.seasonepisodes.api.model

data class SeasonWithEpisodes(
    val seasonName: String,
    val episodes: List<Episode>
)
