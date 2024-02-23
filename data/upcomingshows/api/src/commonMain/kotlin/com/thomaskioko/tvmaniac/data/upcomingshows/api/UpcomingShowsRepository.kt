package com.thomaskioko.tvmaniac.data.upcomingshows.api

import androidx.paging.PagingData
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.shows.api.ShowEntity
import kotlinx.coroutines.flow.Flow

interface UpcomingShowsRepository {
  suspend fun fetchUpcomingShows(forceRefresh: Boolean = false): List<ShowEntity>

  fun observeUpcomingShows(): Flow<Either<Failure, List<ShowEntity>>>

  fun getPagedUpcomingShows(): Flow<PagingData<ShowEntity>>
}
