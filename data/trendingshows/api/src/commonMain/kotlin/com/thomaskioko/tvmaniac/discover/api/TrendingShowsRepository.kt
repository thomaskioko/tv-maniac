package com.thomaskioko.tvmaniac.discover.api

import androidx.paging.PagingData
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow

const val DEFAULT_API_PAGE: Long = 1

interface TrendingShowsRepository {

    suspend fun fetchTrendingShows(
        forceRefresh: Boolean,
    )

    fun observeTrendingShows(
        page: Long = DEFAULT_API_PAGE,
    ): Flow<List<ShowEntity>>

    fun getPagedTrendingShows(forceRefresh: Boolean = false): Flow<PagingData<ShowEntity>>
}
