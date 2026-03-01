package com.thomaskioko.tvmaniac.data.upcomingshows.api

import androidx.paging.PagingData
import com.thomaskioko.tvmaniac.shows.api.model.DEFAULT_API_PAGE
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow

public interface UpcomingShowsRepository {
    public suspend fun fetchUpcomingShows(
        forceRefresh: Boolean,
        page: Long = DEFAULT_API_PAGE,
    )

    public fun observeUpcomingShows(page: Long = DEFAULT_API_PAGE): Flow<List<ShowEntity>>

    public fun getPagedUpcomingShows(forceRefresh: Boolean = false): Flow<PagingData<ShowEntity>>
}
