package com.thomaskioko.tvmaniac.data.upcomingshows.api

import androidx.paging.PagingData
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow

interface UpcomingShowsRepository {
  suspend fun observeUpcomingShows(
    forceRefresh: Boolean = false
  ): Flow<Either<Failure, List<ShowEntity>>>

  fun getPagedUpcomingShows(forceRefresh: Boolean = false): Flow<PagingData<ShowEntity>>
}
