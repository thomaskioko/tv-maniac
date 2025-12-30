package com.thomaskioko.tvmaniac.discover.api

import androidx.paging.PagingData
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow

public const val DEFAULT_API_PAGE: Long = 1

public interface TrendingShowsRepository {

    public suspend fun fetchTrendingShows(
        forceRefresh: Boolean,
    )

    public fun observeTrendingShows(
        page: Long = DEFAULT_API_PAGE,
    ): Flow<List<ShowEntity>>

    public fun getPagedTrendingShows(forceRefresh: Boolean = false): Flow<PagingData<ShowEntity>>
}
