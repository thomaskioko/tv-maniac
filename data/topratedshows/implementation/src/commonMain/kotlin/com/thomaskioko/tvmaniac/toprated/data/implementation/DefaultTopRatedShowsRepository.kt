package com.thomaskioko.tvmaniac.toprated.data.implementation

import androidx.paging.Pager
import androidx.paging.PagingData
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.mapResult
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.core.paging.CommonPagingConfig.pagingConfig
import com.thomaskioko.tvmaniac.core.paging.PaginatedRemoteMediator
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.TOP_RATED_SHOWS
import com.thomaskioko.tvmaniac.shows.api.ShowEntity
import com.thomaskioko.tvmaniac.tmdb.api.DEFAULT_API_PAGE
import com.thomaskioko.tvmaniac.topratedshows.data.api.TopRatedShowsDao
import com.thomaskioko.tvmaniac.topratedshows.data.api.TopRatedShowsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.impl.extensions.fresh
import org.mobilenativefoundation.store.store5.impl.extensions.get

@Inject
class DefaultTopRatedShowsRepository(
  private val store: TopRatedShowsStore,
  private val requestManagerRepository: RequestManagerRepository,
  private val dao: TopRatedShowsDao,
  private val dispatchers: AppCoroutineDispatchers,
) : TopRatedShowsRepository {

  override suspend fun observeTopRatedShows(
    forceRefresh: Boolean,
  ): Flow<Either<Failure, List<ShowEntity>>> {
    val refresh =
      forceRefresh ||
        requestManagerRepository.isRequestExpired(
          entityId = DEFAULT_API_PAGE,
          requestType = TOP_RATED_SHOWS.name,
          threshold = TOP_RATED_SHOWS.duration,
        )
    return store
      .stream(
        StoreReadRequest.cached(
          key = DEFAULT_API_PAGE,
          refresh = refresh,
        ),
      )
      .mapResult(getShows())
      .flowOn(dispatchers.io)
  }

  override fun getPagedTopRatedShows(): Flow<PagingData<ShowEntity>> {
    return Pager(
        config = pagingConfig,
        remoteMediator =
          PaginatedRemoteMediator(
            getLastPage = dao::getLastPage,
            deleteLocalEntity = store::clear,
            fetch = store::fresh,
          ),
        pagingSourceFactory = dao::getPagedTopRatedShows,
      )
      .flow
  }

  private suspend fun getShows(): List<ShowEntity> = store.get(key = DEFAULT_API_PAGE)
}
