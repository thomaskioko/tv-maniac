package com.thomaskioko.tvmaniac.toprated.data.implementation

import androidx.paging.Pager
import androidx.paging.PagingData
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.KermitLogger
import com.thomaskioko.tvmaniac.core.networkutil.mapResult
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.core.paging.CommonPagingConfig.pagingConfig
import com.thomaskioko.tvmaniac.core.paging.FetchResult
import com.thomaskioko.tvmaniac.core.paging.PaginatedRemoteMediator
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.TOP_RATED_SHOWS
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import com.thomaskioko.tvmaniac.tmdb.api.DEFAULT_API_PAGE
import com.thomaskioko.tvmaniac.topratedshows.data.api.TopRatedShowsDao
import com.thomaskioko.tvmaniac.topratedshows.data.api.TopRatedShowsRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.impl.extensions.fresh
import org.mobilenativefoundation.store.store5.impl.extensions.get
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultTopRatedShowsRepository(
  private val store: TopRatedShowsStore,
  private val requestManagerRepository: RequestManagerRepository,
  private val dao: TopRatedShowsDao,
  private val kermitLogger: KermitLogger,
  private val dispatchers: AppCoroutineDispatchers,
) : TopRatedShowsRepository {

  override suspend fun observeTopRatedShows(
    forceRefresh: Boolean
  ): Flow<Either<Failure, List<ShowEntity>>> {
    val refresh = forceRefresh || isRequestExpired(DEFAULT_API_PAGE)
    return store
      .stream(StoreReadRequest.cached(key = DEFAULT_API_PAGE, refresh = refresh))
      .mapResult(getShows())
      .flowOn(dispatchers.io)
  }

  override fun getPagedTopRatedShows(forceRefresh: Boolean): Flow<PagingData<ShowEntity>> {
    return Pager(
        config = pagingConfig,
        remoteMediator = PaginatedRemoteMediator { page -> fetchPage(page, forceRefresh) },
        pagingSourceFactory = dao::getPagedTopRatedShows
      )
      .flow
  }

  private suspend fun fetchPage(page: Long, forceRefresh: Boolean): FetchResult {
    return if (shouldFetchPage(page, forceRefresh)) {
      try {
        val result = store.fresh(page)
        updateRequestManager(page)
        FetchResult.Success(endOfPaginationReached = result.isEmpty())
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        kermitLogger.error("Error while fetching from TopRatedShows RemoteMediator", e)
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
      entityId = page,
      requestType = TOP_RATED_SHOWS.name,
      threshold = TOP_RATED_SHOWS.duration
    )
  }

  private fun updateRequestManager(page: Long) {
    requestManagerRepository.upsert(entityId = page, requestType = TOP_RATED_SHOWS.name)
  }

  private suspend fun getShows(): List<ShowEntity> = store.get(key = DEFAULT_API_PAGE)
}
