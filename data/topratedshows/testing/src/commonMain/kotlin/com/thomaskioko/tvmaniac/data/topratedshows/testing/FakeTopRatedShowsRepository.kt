package com.thomaskioko.tvmaniac.data.topratedshows.testing

import androidx.paging.PagingData
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import com.thomaskioko.tvmaniac.topratedshows.data.api.TopRatedShowsRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class FakeTopRatedShowsRepository : TopRatedShowsRepository {

  private var entityListResult: Channel<Either<Failure, List<ShowEntity>>> =
    Channel(Channel.UNLIMITED)
  private var pagedList: Channel<PagingData<ShowEntity>> = Channel(Channel.UNLIMITED)

  suspend fun setTopRatedShows(result: Either<Failure, List<ShowEntity>>) {
    entityListResult.send(result)
  }

  suspend fun setPagedData(result: PagingData<ShowEntity>) {
    pagedList.send(result)
  }

  override suspend fun observeTopRatedShows(
    forceRefresh: Boolean
  ): Flow<Either<Failure, List<ShowEntity>>> {
    return entityListResult.receiveAsFlow()
  }

  override fun getPagedTopRatedShows(forceRefresh: Boolean): Flow<PagingData<ShowEntity>> {
    return pagedList.receiveAsFlow()
  }
}
