package com.thomaskioko.tvmaniac.data.popularshows.api

import androidx.paging.PagingData
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow

const val DEFAULT_API_PAGE: Long = 1

interface PopularShowsRepository {

  suspend fun fetchPopularShows(
    forceRefresh: Boolean = false,
  )

  fun observePopularShows(
    page: Long = DEFAULT_API_PAGE,
  ): Flow<List<ShowEntity>>

  fun getPagedPopularShows(forceRefresh: Boolean = false): Flow<PagingData<ShowEntity>>
}
