package com.thomaskioko.tvmaniac.data.upcomingshows.api

import androidx.paging.PagingData
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow

public const val DEFAULT_API_PAGE: Long = 1

public interface UpcomingShowsRepository {
    public suspend fun fetchUpcomingShows(forceRefresh: Boolean)

    public fun observeUpcomingShows(page: Long = DEFAULT_API_PAGE): Flow<List<ShowEntity>>

    public fun getPagedUpcomingShows(forceRefresh: Boolean = false): Flow<PagingData<ShowEntity>>
}
