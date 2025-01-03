package com.thomaskioko.tvmaniac.data.popularshows.testing

import androidx.paging.PagingData
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.data.popularshows.api.PopularShowsRepository
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.receiveAsFlow

class FakePopularShowsRepository : PopularShowsRepository {

  private var entityListResult: Channel<Either<Failure, List<ShowEntity>>> =
    Channel(Channel.UNLIMITED)
  private var pagedList: Channel<PagingData<ShowEntity>> = Channel(Channel.UNLIMITED)

  suspend fun setPopularShows(result: Either<Failure, List<ShowEntity>>) {
    entityListResult.send(result)
  }

  suspend fun setPagedData(result: PagingData<ShowEntity>) {
    pagedList.send(result)
  }

  override suspend fun observePopularShows(
    forceRefresh: Boolean
  ): Flow<Either<Failure, List<ShowEntity>>> {
    return entityListResult.receiveAsFlow()
  }

  override fun getPagedPopularShows(forceRefresh: Boolean): Flow<PagingData<ShowEntity>> {
    return flowOf(PagingData.empty())
  }
}
