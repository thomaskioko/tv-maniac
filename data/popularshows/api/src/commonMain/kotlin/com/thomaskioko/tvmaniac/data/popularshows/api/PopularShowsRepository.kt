package com.thomaskioko.tvmaniac.data.popularshows.api

import androidx.paging.PagingData
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.shows.api.ShowEntity
import kotlinx.coroutines.flow.Flow

interface PopularShowsRepository {
  suspend fun fetchPopularShows(forceRefresh: Boolean = false): List<ShowEntity>

  fun observePopularShows(): Flow<Either<Failure, List<ShowEntity>>>

  fun getPagedPopularShows(): Flow<PagingData<ShowEntity>>
}
