package com.thomaskioko.tvmaniac.episodes.api.model

data class EpisodeUiModel(
    val id: Long,
    val seasonId: Long,
    val name: String,
    val overview: String,
    val imageUrl: String,
    val voteAverage: Double,
    val voteCount: Int,
    val episodeNumber: String,
)
