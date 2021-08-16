package com.thomaskioko.tvmaniac.datasource.repository.tvshow

import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory
import com.thomaskioko.tvmaniac.datasource.enums.TimeWindow
import com.thomaskioko.tvmaniac.presentation.model.TvShow
import kotlinx.coroutines.flow.Flow

interface TvShowsRepository  {

    fun getTvShow(tvShowId: Int) : Flow<TvShow>

    suspend fun getPopularTvShows(page: Int) : List<TvShow>

    suspend fun getTopRatedTvShows(page: Int) : List<TvShow>

    suspend fun getTrendingShows(timeWindow: String): List<TvShow>

    suspend fun getFeaturedShows(): List<TvShow>

    suspend fun getShowsByCategory(category: ShowCategory) : List<TvShow>

    fun getWatchlist(): Flow<List<TvShow>>

    suspend fun updateWatchlist(showId: Int, addToWatchList: Boolean)

    suspend fun getShowsByCategoryAndWindow(category: ShowCategory, timeWindow: TimeWindow): List<TvShow>
}