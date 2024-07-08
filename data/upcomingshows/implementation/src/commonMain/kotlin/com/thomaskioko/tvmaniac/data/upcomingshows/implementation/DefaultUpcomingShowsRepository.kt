package com.thomaskioko.tvmaniac.data.upcomingshows.implementation

import androidx.paging.Pager
import androidx.paging.PagingData
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.KermitLogger
import com.thomaskioko.tvmaniac.core.networkutil.mapResult
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.core.paging.CommonPagingConfig.pagingConfig
import com.thomaskioko.tvmaniac.core.paging.PaginatedRemoteMediator
import com.thomaskioko.tvmaniac.data.upcomingshows.api.UpcomingShowsDao
import com.thomaskioko.tvmaniac.data.upcomingshows.api.UpcomingShowsRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.UPCOMING_SHOWS
import com.thomaskioko.tvmaniac.shows.api.ShowEntity
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

@Inject
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
    forceRefresh: Boolean,
  ): Flow<Either<Failure, List<ShowEntity>>> {
    val refresh =
      forceRefresh ||
        requestManagerRepository.isRequestExpired(
          entityId = params.page,
          requestType = UPCOMING_SHOWS.name,
          threshold = UPCOMING_SHOWS.duration,
        )
    return store
      .stream(
        StoreReadRequest.cached(
          key = params,
          refresh = refresh,
        ),
      )
      .mapResult(getShows(params))
      .flowOn(dispatchers.io)
  }

  override fun getPagedUpcomingShows(): Flow<PagingData<ShowEntity>> {
    return Pager(
        config = pagingConfig,
        remoteMediator =
          PaginatedRemoteMediator { page ->
            try {
              store.fresh(
                key =
                  UpcomingParams(
                    startDate = dateFormatter.formatDate(startOfDay.toEpochMilliseconds()),
                    endDate =
                      dateFormatter.formatDate(
                        startOfDay.plus(122.days).toEpochMilliseconds(),
                      ),
                    page = page,
                  ),
              )
            } catch (cancellationException: CancellationException) {
              throw cancellationException
            } catch (throwable: Throwable) {
              kermitLogger.error(
                "Error while fetching from UpcomingShows RemoteMediator",
                throwable
              )
              throw throwable
            }
          },
        pagingSourceFactory = dao::getPagedUpcomingShows,
      )
      .flow
  }

  private suspend fun getShows(params: UpcomingParams): List<ShowEntity> = store.get(key = params)
}

data class UpcomingParams(
  val startDate: String,
  val endDate: String,
  val page: Long,
  val sortBy: String = DEFAULT_SORT_ORDER,
)
