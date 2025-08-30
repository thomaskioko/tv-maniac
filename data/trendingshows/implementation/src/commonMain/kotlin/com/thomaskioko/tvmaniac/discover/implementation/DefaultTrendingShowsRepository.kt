package com.thomaskioko.tvmaniac.discover.implementation

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingData
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.paging.CommonPagingConfig
import com.thomaskioko.tvmaniac.core.paging.FetchResult
import com.thomaskioko.tvmaniac.core.paging.PaginatedRemoteMediator
import com.thomaskioko.tvmaniac.discover.api.TrendingShowsDao
import com.thomaskioko.tvmaniac.discover.api.TrendingShowsParams
import com.thomaskioko.tvmaniac.discover.api.TrendingShowsRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.TRENDING_SHOWS_TODAY
import com.thomaskioko.tvmaniac.shows.api.model.DEFAULT_WEEK_TIME_WINDOW
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import com.thomaskioko.tvmaniac.tmdb.api.DEFAULT_API_PAGE
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import org.mobilenativefoundation.store.store5.impl.extensions.fresh
import org.mobilenativefoundation.store.store5.impl.extensions.get

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultTrendingShowsRepository(
    private val store: TrendingShowsStore,
    private val requestManagerRepository: RequestManagerRepository,
    private val dao: TrendingShowsDao,
    private val logger: Logger,
) : TrendingShowsRepository {

    override fun observeTrendingShows(page: Long): Flow<List<ShowEntity>> = dao.observeTrendingShows(page)

    override suspend fun fetchTrendingShows(forceRefresh: Boolean) {
        val page = DEFAULT_API_PAGE
        val param = TrendingShowsParams(timeWindow = DEFAULT_WEEK_TIME_WINDOW, page = page)
        when {
            forceRefresh -> store.fresh(param)
            else -> store.get(param)
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getPagedTrendingShows(forceRefresh: Boolean): Flow<PagingData<ShowEntity>> {
        return Pager(
            config = CommonPagingConfig.pagingConfig,
            remoteMediator = PaginatedRemoteMediator { page -> fetchPage(page, forceRefresh) },
            pagingSourceFactory = dao::getPagedTrendingShows,
        )
            .flow
    }

    private suspend fun fetchPage(page: Long, forceRefresh: Boolean): FetchResult {
        return if (shouldFetchPage(page, forceRefresh)) {
            try {
                val result = store.fresh(TrendingShowsParams(timeWindow = DEFAULT_WEEK_TIME_WINDOW, page = page))
                updateRequestManager(page)
                FetchResult.Success(endOfPaginationReached = result.isEmpty())
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                logger.error("Error while fetching from TrendingShows RemoteMediator", e)
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
            requestType = TRENDING_SHOWS_TODAY.name,
            threshold = TRENDING_SHOWS_TODAY.duration,
        )
    }

    private fun updateRequestManager(page: Long) {
        requestManagerRepository.upsert(
            entityId = page,
            requestType = TRENDING_SHOWS_TODAY.name,
        )
    }
}
