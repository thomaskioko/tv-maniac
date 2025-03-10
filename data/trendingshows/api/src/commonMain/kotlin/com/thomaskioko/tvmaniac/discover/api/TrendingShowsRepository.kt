package com.thomaskioko.tvmaniac.discover.api

import androidx.paging.PagingData
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow

interface TrendingShowsRepository {
  suspend fun observeTrendingShows(
    forceRefresh: Boolean = false
  ): Flow<Either<Failure, List<ShowEntity>>>

  fun getPagedTrendingShows(forceRefresh: Boolean = false): Flow<PagingData<ShowEntity>>
}
