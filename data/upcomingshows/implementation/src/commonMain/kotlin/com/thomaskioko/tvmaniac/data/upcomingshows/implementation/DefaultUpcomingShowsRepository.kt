package com.thomaskioko.tvmaniac.data.upcomingshows.implementation

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingData
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.paging.CommonPagingConfig.pagingConfig
import com.thomaskioko.tvmaniac.core.paging.FetchResult
import com.thomaskioko.tvmaniac.core.paging.PaginatedRemoteMediator
import com.thomaskioko.tvmaniac.data.upcomingshows.api.UpcomingShowsDao
import com.thomaskioko.tvmaniac.data.upcomingshows.api.UpcomingShowsRepository
import com.thomaskioko.tvmaniac.data.upcomingshows.implementation.model.UpcomingParams
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.UPCOMING_SHOWS
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.impl.extensions.fresh
import org.mobilenativefoundation.store.store5.impl.extensions.get
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.time.Duration.Companion.days

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultUpcomingShowsRepository(
    private val dateTimeProvider: DateTimeProvider,
    private val store: UpcomingShowsStore,
    private val dao: UpcomingShowsDao,
    private val requestManagerRepository: RequestManagerRepository,
    private val logger: Logger,
) : UpcomingShowsRepository {

    private val startOfDay get() = dateTimeProvider.startOfDay()

    private val duration get() = dateTimeProvider.formatDate(startOfDay.plus(DateTimePeriod(days = 122), TimeZone.currentSystemDefault()).toEpochMilliseconds())

    override suspend fun fetchUpcomingShows(forceRefresh: Boolean) {
        val page = 1L // Always fetch first page for non-paginated requests
        // TODO:: Load this from duration repository. Default range is 4 months
        val params = UpcomingParams(
            startDate = dateTimeProvider.formatDate(startOfDay.toEpochMilliseconds()),
            endDate = duration,
            page = page,
        )
        when {
            forceRefresh -> store.fresh(params)
            else -> store.get(params)
        }
    }

    override fun observeUpcomingShows(page: Long): Flow<List<ShowEntity>> = dao.observeUpcomingShows(page)

    @OptIn(ExperimentalPagingApi::class)
    override fun getPagedUpcomingShows(forceRefresh: Boolean): Flow<PagingData<ShowEntity>> {
        return Pager(
            config = pagingConfig,
            remoteMediator = PaginatedRemoteMediator { page -> fetchPage(page, forceRefresh) },
            pagingSourceFactory = dao::getPagedUpcomingShows,
        )
            .flow
    }

    private suspend fun fetchPage(page: Long, forceRefresh: Boolean): FetchResult {
        return if (forceRefresh || !dao.pageExists(page)) {
            try {
                val result = store.fresh(
                    UpcomingParams(
                        startDate = dateTimeProvider.formatDate(startOfDay.toEpochMilliseconds()),
                        endDate = duration,
                        page = page,
                    ),
                )
                updateRequestManager(page)
                FetchResult.Success(endOfPaginationReached = result.isEmpty())
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                logger.error("Error while fetching from UpcomingShows RemoteMediator", e)
                FetchResult.Error(e)
            }
        } else {
            FetchResult.NoFetch
        }
    }

    private fun updateRequestManager(page: Long) {
        requestManagerRepository.upsert(entityId = page, requestType = UPCOMING_SHOWS.name)
    }
}
