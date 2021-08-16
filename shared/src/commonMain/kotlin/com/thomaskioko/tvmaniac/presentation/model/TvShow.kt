package com.thomaskioko.tvmaniac.presentation.model

import com.thomaskioko.tvmaniac.datasource.enums.TimeWindow
import com.thomaskioko.tvmaniac.datasource.enums.TvShowCategory

data class TvShow(
    val id: Int = 0,
    val title: String = "",
    val overview: String = "",
    val language: String = "",
    val posterImageUrl: String = "",
    val backdropImageUrl: String = "",
    val year : String = "",
    val status : String = "",
    val votes: Int = 0,
    val averageVotes: Double = 0.0,
    val isInWatchlist: Boolean = false,
    val genreIds: List<Int> = listOf(),
    val showCategory: TvShowCategory = TvShowCategory.POPULAR_TV_SHOWS,
    val timeWindow : TimeWindow = TimeWindow.WEEK,
    val seasonsList: List<Season> = emptyList()
) {
    companion object {
        val EMPTY_SHOW = TvShow()
    }
}