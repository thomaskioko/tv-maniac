package com.thomaskioko.tvmaniac.data.upcomingshows.testing

import androidx.paging.PagingData
import com.thomaskioko.tvmaniac.data.upcomingshows.api.UpcomingShowsRepository
import com.thomaskioko.tvmaniac.shows.api.ShowEntity
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class FakeUpcomingShowsRepository : UpcomingShowsRepository {

  private var showEntityList: Channel<List<ShowEntity>> = Channel(Channel.UNLIMITED)
  private var entityListResult: Channel<Either<Failure, List<ShowEntity>>> =
    Channel(Channel.UNLIMITED)
  private var pagedList: Channel<PagingData<ShowEntity>> = Channel(Channel.UNLIMITED)

  suspend fun setUpcomingShows(result: List<ShowEntity>) {
    showEntityList.send(result)
  }

  suspend fun setObserveUpcomingShows(result: Either<Failure, List<ShowEntity>>) {
    entityListResult.send(result)
  }

  suspend fun setPagedData(result: PagingData<ShowEntity>) {
    pagedList.send(result)
  }

  override suspend fun fetchUpcomingShows(forceRefresh: Boolean): List<ShowEntity> {
    return showEntityList.receive()
  }

  override fun observeUpcomingShows(): Flow<Either<Failure, List<ShowEntity>>> {
    return entityListResult.receiveAsFlow()
  }

  override fun getPagedUpcomingShows(): Flow<PagingData<ShowEntity>> {
    return pagedList.receiveAsFlow()
  }
}
