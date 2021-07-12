package com.thomaskioko.tvmaniac.datasource.cache.model

data class TvShowsEntity(
    val id: Int = 0,
    val showId : Int,
    val title: String,
    val description: String,
    val language: String,
    val imageUrl: String,
    val votes: Int,
    val averageVotes: Double,
    val genreIds: List<Int> = listOf()
)