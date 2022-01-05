package com.thomaskioko.tvmaniac.datasource.repository.tvshow

import com.kuuurt.paging.multiplatform.PagingData
import com.thomaskioko.tvmaniac.datasource.cache.Show
import com.thomaskioko.tvmaniac.datasource.repository.util.Resource
import com.thomaskioko.tvmaniac.util.CommonFlow
import kotlinx.coroutines.flow.Flow

interface TvShowsRepository {

    suspend fun updateWatchlist(showId: Int, addToWatchList: Boolean)

    fun observeShow(tvShowId: Int): Flow<Resource<Show>>

    fun observeWatchlist(): Flow<List<Show>>

    fun observeShowsByCategoryID(
        categoryId: Int
    ): Flow<Resource<List<Show>>>

    fun observePagedShowsByCategoryID(
        categoryId: Int
    ): CommonFlow<PagingData<Show>>
}
