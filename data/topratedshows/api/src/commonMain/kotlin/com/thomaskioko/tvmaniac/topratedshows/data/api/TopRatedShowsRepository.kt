package com.thomaskioko.tvmaniac.topratedshows.data.api

import androidx.paging.PagingData
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow

const val DEFAULT_API_PAGE: Long = 1

interface TopRatedShowsRepository {
  suspend fun fetchTopRatedShows(
    forceRefresh: Boolean,
  )

  fun observeTopRatedShows(
    page: Long = DEFAULT_API_PAGE,
  ): Flow<List<ShowEntity>>

  fun getPagedTopRatedShows(forceRefresh: Boolean = false): Flow<PagingData<ShowEntity>>
}
