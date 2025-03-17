package com.thomaskioko.tvmaniac.data.upcomingshows.implementation

import androidx.paging.Pager
import androidx.paging.PagingData
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.KermitLogger
import com.thomaskioko.tvmaniac.core.store.mapToEither
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.core.paging.CommonPagingConfig.pagingConfig
import com.thomaskioko.tvmaniac.core.paging.FetchResult
import com.thomaskioko.tvmaniac.core.paging.PaginatedRemoteMediator
import com.thomaskioko.tvmaniac.data.upcomingshows.api.UpcomingShowsDao
import com.thomaskioko.tvmaniac.data.upcomingshows.api.UpcomingShowsRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.UPCOMING_SHOWS
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import com.thomaskioko.tvmaniac.tmdb.api.DEFAULT_API_PAGE
import com.thomaskioko.tvmaniac.tmdb.api.DEFAULT_SORT_ORDER
import com.thomaskioko.tvmaniac.util.PlatformDateFormatter
import com.thomaskioko.tvmaniac.util.startOfDay
import kotlin.time.Duration.Companion.days
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
class DefaultUpcomingShowsRepository(
  private val dateFormatter: PlatformDateFormatter,
  private val store: UpcomingShowsStore,
  private val dao: UpcomingShowsDao,
  private val requestManagerRepository: RequestManagerRepository,
  private val kermitLogger: KermitLogger,
  private val dispatchers: AppCoroutineDispatchers,
) : UpcomingShowsRepository {

  // TODO:: Load this from duration repository. Default range is 4 months
  private val params =
    UpcomingParams(
      startDate = dateFormatter.formatDate(startOfDay.toEpochMilliseconds()),
      endDate = dateFormatter.formatDate(startOfDay.plus(122.days).toEpochMilliseconds()),
      page = DEFAULT_API_PAGE,
    )

  override suspend fun observeUpcomingShows(
    forceRefresh: Boolean
  ): Flow<Either<Failure, List<ShowEntity>>> {
    val refresh = forceRefresh || isRequestExpired(params.page)
    return store
      .stream(StoreReadRequest.cached(key = params, refresh = refresh))
      .mapToEither()
      .flowOn(dispatchers.io)
  }

  override fun getPagedUpcomingShows(forceRefresh: Boolean): Flow<PagingData<ShowEntity>> {
    return Pager(
        config = pagingConfig,
        remoteMediator = PaginatedRemoteMediator { page -> fetchPage(page, forceRefresh) },
        pagingSourceFactory = dao::getPagedUpcomingShows
      )
      .flow
  }

  private suspend fun fetchPage(page: Long, forceRefresh: Boolean): FetchResult {
    return if (shouldFetchPage(page, forceRefresh)) {
      try {
        val result =
          store.fresh(
            UpcomingParams(
              startDate = dateFormatter.formatDate(startOfDay.toEpochMilliseconds()),
              endDate = dateFormatter.formatDate(startOfDay.plus(122.days).toEpochMilliseconds()),
              page = page,
            )
          )
        updateRequestManager(page)
        FetchResult.Success(endOfPaginationReached = result.isEmpty())
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        kermitLogger.error("Error while fetching from UpcomingShows RemoteMediator", e)
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
      requestType = UPCOMING_SHOWS.name,
      threshold = UPCOMING_SHOWS.duration,
    )
  }

  private fun updateRequestManager(page: Long) {
    requestManagerRepository.upsert(entityId = page, requestType = UPCOMING_SHOWS.name)
  }

  private suspend fun getShows(params: UpcomingParams): List<ShowEntity> = store.get(key = params)
}

data class UpcomingParams(
  val startDate: String,
  val endDate: String,
  val page: Long,
  val sortBy: String = DEFAULT_SORT_ORDER,
)
