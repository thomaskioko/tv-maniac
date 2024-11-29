package com.thomaskioko.tvmaniac.discover.implementation

import androidx.paging.Pager
import androidx.paging.PagingData
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.KermitLogger
import com.thomaskioko.tvmaniac.core.networkutil.mapResult
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.core.paging.CommonPagingConfig
import com.thomaskioko.tvmaniac.core.paging.FetchResult
import com.thomaskioko.tvmaniac.core.paging.PaginatedRemoteMediator
import com.thomaskioko.tvmaniac.discover.api.TrendingShowsDao
import com.thomaskioko.tvmaniac.discover.api.TrendingShowsParams
import com.thomaskioko.tvmaniac.discover.api.TrendingShowsRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.TRENDING_SHOWS_TODAY
import com.thomaskioko.tvmaniac.shows.api.DEFAULT_DAY_TIME_WINDOW
import com.thomaskioko.tvmaniac.shows.api.ShowEntity
import com.thomaskioko.tvmaniac.tmdb.api.DEFAULT_API_PAGE
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.impl.extensions.fresh
import org.mobilenativefoundation.store.store5.impl.extensions.get
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

@Inject
@ContributesBinding(AppScope::class)
class DefaultTrendingShowsRepository(
  private val store: TrendingShowsStore,
  private val requestManagerRepository: RequestManagerRepository,
  private val dao: TrendingShowsDao,
  private val kermitLogger: KermitLogger,
  private val dispatchers: AppCoroutineDispatchers,
) : TrendingShowsRepository {

  override suspend fun observeTrendingShows(
    forceRefresh: Boolean
  ): Flow<Either<Failure, List<ShowEntity>>> {
    val refresh = forceRefresh || isRequestExpired(DEFAULT_API_PAGE)
    return store
      .stream(
        StoreReadRequest.cached(
          key = TrendingShowsParams(timeWindow = DEFAULT_DAY_TIME_WINDOW, page = DEFAULT_API_PAGE),
          refresh = refresh
        )
      )
      .mapResult(getShows())
      .flowOn(dispatchers.io)
  }

  override fun getPagedTrendingShows(forceRefresh: Boolean): Flow<PagingData<ShowEntity>> {
    return Pager(
        config = CommonPagingConfig.pagingConfig,
        remoteMediator = PaginatedRemoteMediator { page -> fetchPage(page, forceRefresh) },
        pagingSourceFactory = dao::getPagedTrendingShows
      )
      .flow
  }

  private suspend fun fetchPage(page: Long, forceRefresh: Boolean): FetchResult {
    return if (shouldFetchPage(page, forceRefresh)) {
      try {
        val result =
          store.fresh(TrendingShowsParams(timeWindow = DEFAULT_DAY_TIME_WINDOW, page = page))
        updateRequestManager(page)
        FetchResult.Success(endOfPaginationReached = result.isEmpty())
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        kermitLogger.error("Error while fetching from TrendingShows RemoteMediator", e)
        FetchResult.Error(e)
      }
    } else {
      FetchResult.NoFetch
    }
  }

  private fun shouldFetchPage(page: Long, forceRefresh: Boolean): Boolean {
    if (forceRefresh) return true
    val pageExists = dao.pageExists(page)
    return !pageExists || isRequestExpired(page)
  }

  private fun isRequestExpired(page: Long): Boolean {
    return requestManagerRepository.isRequestExpired(
      entityId = TRENDING_SHOWS_TODAY.requestId + page,
      requestType = TRENDING_SHOWS_TODAY.name,
      threshold = TRENDING_SHOWS_TODAY.duration
    )
  }

  private suspend fun updateRequestManager(page: Long) {
    requestManagerRepository.upsert(
      entityId = TRENDING_SHOWS_TODAY.requestId + page,
      requestType = TRENDING_SHOWS_TODAY.name
    )
  }

  private suspend fun getShows(): List<ShowEntity> =
    store.get(
      key = TrendingShowsParams(timeWindow = DEFAULT_DAY_TIME_WINDOW, page = DEFAULT_API_PAGE)
    )
}
