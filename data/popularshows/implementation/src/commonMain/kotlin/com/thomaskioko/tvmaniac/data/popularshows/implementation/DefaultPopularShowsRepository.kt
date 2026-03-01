package com.thomaskioko.tvmaniac.data.popularshows.implementation

import androidx.paging.Pager
import androidx.paging.PagingData
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.paging.CommonPagingConfig.pagingConfig
import com.thomaskioko.tvmaniac.core.paging.FetchResult
import com.thomaskioko.tvmaniac.core.paging.PaginatedRemoteMediator
import com.thomaskioko.tvmaniac.data.popularshows.api.PopularShowsDao
import com.thomaskioko.tvmaniac.data.popularshows.api.PopularShowsRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.POPULAR_SHOWS
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.impl.extensions.fresh
import org.mobilenativefoundation.store.store5.impl.extensions.get
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultPopularShowsRepository(
    private val store: PopularShowsStore,
    private val popularShowsDao: PopularShowsDao,
    private val requestManagerRepository: RequestManagerRepository,
    private val logger: Logger,
) : PopularShowsRepository {

    override suspend fun fetchPopularShows(forceRefresh: Boolean, page: Long) {
        when {
            forceRefresh -> store.fresh(page)
            else -> store.get(page)
        }
    }

    override fun observePopularShows(page: Long): Flow<List<ShowEntity>> = popularShowsDao.observePopularShows(page)

    override fun getPagedPopularShows(forceRefresh: Boolean): Flow<PagingData<ShowEntity>> {
        return Pager(
            config = pagingConfig,
            remoteMediator = PaginatedRemoteMediator { page -> fetchPage(page, forceRefresh) },
            pagingSourceFactory = popularShowsDao::getPagedPopularShows,
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
                logger.error("Error while fetching from PopularShows RemoteMediator", e)
                FetchResult.Error(e)
            }
        } else {
            FetchResult.NoFetch
        }
    }

    private fun shouldFetchPage(page: Long, forceRefresh: Boolean): Boolean {
        if (forceRefresh) return true
        val pageExists = popularShowsDao.pageExists(page)
        return !pageExists || isRequestExpired(page)
    }

    private fun isRequestExpired(page: Long): Boolean {
        return requestManagerRepository.isRequestExpired(
            entityId = page,
            requestType = POPULAR_SHOWS.name,
            threshold = POPULAR_SHOWS.duration,
        )
    }

    private fun updateRequestManager(page: Long) {
        requestManagerRepository.upsert(entityId = page, requestType = POPULAR_SHOWS.name)
    }
}
