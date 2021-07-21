package com.thomaskioko.tvmaniac.datasource.cache.model

import com.thomaskioko.tvmaniac.datasource.enums.TimeWindow
import com.thomaskioko.tvmaniac.datasource.enums.TvShowCategory

data class TvShowsEntity(
    val id: Int,
    val title: String,
    val description: String,
    val language: String,
    val imageUrl: String,
    val votes: Int,
    val averageVotes: Double,
    val genreIds: List<Int> = listOf(),
    val showCategory: TvShowCategory = TvShowCategory.POPULAR_TV_SHOWS,
    val timeWindow : TimeWindow = TimeWindow.WEEK,
    val seasonsList: List<SeasonsEntity> = emptyList()
)