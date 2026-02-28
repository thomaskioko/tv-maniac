package com.thomaskioko.tvmaniac.discover.api

import androidx.paging.PagingData
import com.thomaskioko.tvmaniac.shows.api.model.DEFAULT_API_PAGE
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow

public interface TrendingShowsRepository {

    public suspend fun fetchTrendingShows(
        forceRefresh: Boolean,
        page: Long = DEFAULT_API_PAGE,
    )

    public fun observeTrendingShows(
        page: Long = DEFAULT_API_PAGE,
    ): Flow<List<ShowEntity>>

    public fun getPagedTrendingShows(forceRefresh: Boolean = false): Flow<PagingData<ShowEntity>>
}
