package com.thomaskioko.tvmaniac.datasource.cache.model

import kotlinx.serialization.Serializable

@Serializable
data class SeasonsEntity(
    val seasonId: Int,
    val tvShowId: Int,
    val name: String,
    val overview: String,
    val seasonNumber: Int,
    val episodeCount: Int,
    val episodeList: List<EpisodeEntity> = emptyList()
)

