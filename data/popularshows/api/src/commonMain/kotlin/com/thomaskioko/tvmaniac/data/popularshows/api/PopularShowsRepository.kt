package com.thomaskioko.tvmaniac.data.popularshows.api

import androidx.paging.PagingData
import com.thomaskioko.tvmaniac.shows.api.model.DEFAULT_API_PAGE
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow

public interface PopularShowsRepository {

    public suspend fun fetchPopularShows(
        forceRefresh: Boolean = false,
        page: Long = DEFAULT_API_PAGE,
    )

    public fun observePopularShows(
        page: Long = DEFAULT_API_PAGE,
    ): Flow<List<ShowEntity>>

    public fun getPagedPopularShows(forceRefresh: Boolean = false): Flow<PagingData<ShowEntity>>
}
