package com.thomaskioko.tvmaniac.episodes.api.model

data class EpisodeUiModel(
    val id: Int,
    val seasonId: Int,
    val name: String,
    val overview: String,
    val imageUrl: String?,
    val ratings: Double,
    val runtime: Int,
    val voteCount: Int,
    val episodeNumber: String,
)
