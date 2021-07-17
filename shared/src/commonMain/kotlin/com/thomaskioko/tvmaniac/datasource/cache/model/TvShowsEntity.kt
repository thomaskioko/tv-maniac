package com.thomaskioko.tvmaniac.datasource.cache.model

data class TvShowsEntity(
    val id: Int,
    val title: String,
    val description: String,
    val language: String,
    val imageUrl: String,
    val votes: Int,
    val averageVotes: Double,
    val genreIds: List<Int> = listOf(),
    val showCategory: TvShowCategory,
    val seasonsList: List<SeasonsEntity> = emptyList()
)