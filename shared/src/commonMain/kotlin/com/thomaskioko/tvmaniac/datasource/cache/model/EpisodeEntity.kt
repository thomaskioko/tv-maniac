package com.thomaskioko.tvmaniac.datasource.cache.model

import kotlinx.serialization.Serializable

@Serializable
data class EpisodeEntity(
    val id: Int,
    val seasonId : Int,
    val name : String,
    val overview : String,
    val seasonNumber : Int,
    val imageUrl : String,
    val voteAverage : Double,
    val voteCount : Int,
    val episodeNumber : Int,
)