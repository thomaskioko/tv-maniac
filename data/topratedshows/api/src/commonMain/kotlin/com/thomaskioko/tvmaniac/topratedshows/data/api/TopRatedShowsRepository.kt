package com.thomaskioko.tvmaniac.topratedshows.data.api

import androidx.paging.PagingData
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.shows.api.ShowEntity
import kotlinx.coroutines.flow.Flow

interface TopRatedShowsRepository {
  suspend fun observeTopRatedShows(
    forceRefresh: Boolean = false,
  ): Flow<Either<Failure, List<ShowEntity>>>

  fun getPagedTopRatedShows(forceRefresh: Boolean = false): Flow<PagingData<ShowEntity>>
}
