package com.thomaskioko.tvmaniac.topratedshows.data.api

import androidx.paging.PagingData
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow

public const val DEFAULT_API_PAGE: Long = 1

public interface TopRatedShowsRepository {
    public suspend fun fetchTopRatedShows(
        forceRefresh: Boolean,
    )

    public fun observeTopRatedShows(
        page: Long = DEFAULT_API_PAGE,
    ): Flow<List<ShowEntity>>

    public fun getPagedTopRatedShows(forceRefresh: Boolean = false): Flow<PagingData<ShowEntity>>
}
