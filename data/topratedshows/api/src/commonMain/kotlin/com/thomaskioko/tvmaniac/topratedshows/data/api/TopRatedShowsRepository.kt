package com.thomaskioko.tvmaniac.topratedshows.data.api

import androidx.paging.PagingData
import com.thomaskioko.tvmaniac.shows.api.model.DEFAULT_API_PAGE
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow

public interface TopRatedShowsRepository {
    public suspend fun fetchTopRatedShows(
        forceRefresh: Boolean,
        page: Long = DEFAULT_API_PAGE,
    )

    public fun observeTopRatedShows(
        page: Long = DEFAULT_API_PAGE,
    ): Flow<List<ShowEntity>>

    public fun getPagedTopRatedShows(forceRefresh: Boolean = false): Flow<PagingData<ShowEntity>>
}
