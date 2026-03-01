package com.thomaskioko.tvmaniac.data.popularshows.testing

import androidx.paging.PagingData
import com.thomaskioko.tvmaniac.data.popularshows.api.PopularShowsRepository
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

public class FakePopularShowsRepository : PopularShowsRepository {
    private val _shows = MutableStateFlow<List<ShowEntity>>(emptyList())
    private val _pagedShows = MutableStateFlow<PagingData<ShowEntity>>(PagingData.empty())

    public fun setPopularShows(result: List<ShowEntity>) {
        _shows.value = result
    }

    override suspend fun fetchPopularShows(forceRefresh: Boolean, page: Long) {
    }

    override fun observePopularShows(page: Long): Flow<List<ShowEntity>> {
        return _shows.asStateFlow()
    }

    override fun getPagedPopularShows(forceRefresh: Boolean): Flow<PagingData<ShowEntity>> {
        return _pagedShows.asStateFlow()
    }
}
