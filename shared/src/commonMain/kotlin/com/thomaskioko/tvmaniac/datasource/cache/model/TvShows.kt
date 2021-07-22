package com.thomaskioko.tvmaniac.datasource.cache.model

import com.thomaskioko.tvmaniac.datasource.enums.TimeWindow
import com.thomaskioko.tvmaniac.datasource.enums.TvShowCategory

data class TvShows(
    val id: Int = 0,
    val title: String = "",
    val overview: String = "",
    val language: String = "",
    val posterImageUrl: String = "",
    val backdropImageUrl: String = "",
    val votes: Int = 0,
    val averageVotes: Double = 0.0,
    val genreIds: List<Int> = listOf(),
    val showCategory: TvShowCategory = TvShowCategory.POPULAR_TV_SHOWS,
    val timeWindow : TimeWindow = TimeWindow.WEEK,
    val seasonsList: List<SeasonsEntity> = emptyList()
) {
    companion object {
        val EMPTY_SHOW = TvShows()
    }
}