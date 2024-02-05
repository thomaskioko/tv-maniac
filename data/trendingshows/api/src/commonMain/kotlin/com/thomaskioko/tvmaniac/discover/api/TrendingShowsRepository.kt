package com.thomaskioko.tvmaniac.discover.api

import androidx.paging.PagingData
import com.thomaskioko.tvmaniac.shows.api.ShowEntity
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.flow.Flow

interface TrendingShowsRepository {
  suspend fun fetchTrendingShows(forceRefresh: Boolean = false): List<ShowEntity>

  fun observeTrendingShows(): Flow<Either<Failure, List<ShowEntity>>>

  fun getPagedTrendingShows(): Flow<PagingData<ShowEntity>>
}
