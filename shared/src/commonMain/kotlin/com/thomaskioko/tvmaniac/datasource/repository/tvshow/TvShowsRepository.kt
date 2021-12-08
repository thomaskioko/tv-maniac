package com.thomaskioko.tvmaniac.datasource.repository.tvshow

import com.kuuurt.paging.multiplatform.PagingData
import com.thomaskioko.tvmaniac.datasource.cache.Show
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory
import com.thomaskioko.tvmaniac.datasource.repository.TrendingShowData
import com.thomaskioko.tvmaniac.util.CommonFlow
import kotlinx.coroutines.flow.Flow

interface TvShowsRepository {

    suspend fun updateWatchlist(showId: Int, addToWatchList: Boolean)

    suspend fun getDiscoverShowList(
        categoryList: List<ShowCategory>
    ): List<TrendingShowData>

    suspend fun getShowsByCategoryId(page: Int, categoryId: Int): List<Show>

    fun getShow(tvShowId: Int): Flow<Show>

    fun getWatchlist(): Flow<List<Show>>

    fun getPagedShowsByCategoryID(
        categoryId: Int
    ): CommonFlow<PagingData<Show>>
}
