package com.thomaskioko.tvmaniac.data.upcomingshows.testing

import androidx.paging.PagingData
import com.thomaskioko.tvmaniac.data.upcomingshows.api.UpcomingShowsRepository
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

public class FakeUpcomingShowsRepository : UpcomingShowsRepository {
    private val shows = MutableStateFlow<List<ShowEntity>>(emptyList())
    private val pagedShows = MutableStateFlow<PagingData<ShowEntity>>(PagingData.empty())

    public fun setUpcomingShows(result: List<ShowEntity>) {
        shows.value = result
    }

    override suspend fun fetchUpcomingShows(forceRefresh: Boolean) {
    }

    override fun observeUpcomingShows(page: Long): Flow<List<ShowEntity>> {
        return shows.asStateFlow()
    }

    override fun getPagedUpcomingShows(forceRefresh: Boolean): Flow<PagingData<ShowEntity>> {
        return pagedShows.asStateFlow()
    }
}
