package com.thomaskioko.tvmaniac.datasource.repository.tvshow

import com.kuuurt.paging.multiplatform.PagingData
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory
import com.thomaskioko.tvmaniac.datasource.enums.TimeWindow
import com.thomaskioko.tvmaniac.presentation.model.TvShow
import com.thomaskioko.tvmaniac.util.CommonFlow
import kotlinx.coroutines.flow.Flow

interface TvShowsRepository {

    fun getTvShow(tvShowId: Int): Flow<TvShow>

    suspend fun getPopularTvShows(page: Int): List<TvShow>

    suspend fun getPagedPopularTvShows(): CommonFlow<PagingData<TvShow>>

    suspend fun getTopRatedTvShows(page: Int): List<TvShow>

    suspend fun getPagedTopRatedTvShows(): CommonFlow<PagingData<TvShow>>

    suspend fun getTrendingShowsByTime(timeWindow: TimeWindow): List<TvShow>

    suspend fun getFeaturedShows(): List<TvShow>

    suspend fun getShowsByCategory(category: ShowCategory): List<TvShow>

    suspend fun getPagedShowsByCategory(category: ShowCategory): CommonFlow<PagingData<TvShow>>

    fun getWatchlist(): Flow<List<TvShow>>

    suspend fun updateWatchlist(showId: Int, addToWatchList: Boolean)

    suspend fun getShowsByCategoryAndWindow(
        category: ShowCategory,
        timeWindow: TimeWindow
    ): List<TvShow>

    suspend fun getPagedShowsByCategoryAndWindow(
        category: ShowCategory,
        timeWindow: TimeWindow
    ): CommonFlow<PagingData<TvShow>>
}