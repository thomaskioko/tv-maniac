package com.thomaskioko.tvmaniac.data.topratedshows.testing

import androidx.paging.PagingData
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import com.thomaskioko.tvmaniac.topratedshows.data.api.TopRatedShowsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

public class FakeTopRatedShowsRepository : TopRatedShowsRepository {
    private val shows = MutableStateFlow<List<ShowEntity>>(emptyList())
    private val pagedShows = MutableStateFlow<PagingData<ShowEntity>>(PagingData.empty())

    public fun setTopRatedShows(result: List<ShowEntity>) {
        shows.value = result
    }

    override suspend fun fetchTopRatedShows(forceRefresh: Boolean, page: Long) {
    }

    override fun observeTopRatedShows(page: Long): Flow<List<ShowEntity>> {
        return shows.asStateFlow()
    }

    override fun getPagedTopRatedShows(forceRefresh: Boolean): Flow<PagingData<ShowEntity>> {
        return pagedShows.asStateFlow()
    }
}
