package com.thomaskioko.tvmaniac.data.upcomingshows.api

import androidx.paging.PagingData
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow

const val DEFAULT_API_PAGE: Long = 1

interface UpcomingShowsRepository {
    suspend fun fetchUpcomingShows(forceRefresh: Boolean)

    fun observeUpcomingShows(page: Long = DEFAULT_API_PAGE): Flow<List<ShowEntity>>

    fun getPagedUpcomingShows(forceRefresh: Boolean = false): Flow<PagingData<ShowEntity>>
}
