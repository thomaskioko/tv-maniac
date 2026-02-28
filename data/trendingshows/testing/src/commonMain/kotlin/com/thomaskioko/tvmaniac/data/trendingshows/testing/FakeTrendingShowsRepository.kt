package com.thomaskioko.tvmaniac.data.trendingshows.testing

import androidx.paging.PagingData
import com.thomaskioko.tvmaniac.discover.api.TrendingShowsRepository
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

public class FakeTrendingShowsRepository : TrendingShowsRepository {

    private val shows = MutableStateFlow<List<ShowEntity>>(emptyList())
    private val pagedShows = MutableStateFlow<PagingData<ShowEntity>>(PagingData.empty())

    public fun setTrendingShows(result: List<ShowEntity>) {
        shows.value = result
    }

    public fun setPagedData(result: PagingData<ShowEntity>) {
        pagedShows.value = result
    }

    override suspend fun fetchTrendingShows(forceRefresh: Boolean, page: Long) {
    }

    override fun observeTrendingShows(page: Long): Flow<List<ShowEntity>> {
        return shows.asStateFlow()
    }

    override fun getPagedTrendingShows(forceRefresh: Boolean): Flow<PagingData<ShowEntity>> {
        return pagedShows.asStateFlow()
    }
}
