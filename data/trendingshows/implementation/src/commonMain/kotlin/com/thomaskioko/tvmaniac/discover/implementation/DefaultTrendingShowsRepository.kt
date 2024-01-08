package com.thomaskioko.tvmaniac.discover.implementation

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import app.cash.paging.Pager
import com.thomaskioko.tvmaniac.discover.api.TrendingShowsDao
import com.thomaskioko.tvmaniac.discover.api.TrendingShowsParams
import com.thomaskioko.tvmaniac.discover.api.TrendingShowsRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.shows.api.DEFAULT_DAY_TIME_WINDOW
import com.thomaskioko.tvmaniac.shows.api.ShowEntity
import com.thomaskioko.tvmaniac.tmdb.api.DEFAULT_API_PAGE
import com.thomaskioko.tvmaniac.util.extensions.filterForResult
import com.thomaskioko.tvmaniac.util.extensions.mapResult
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import com.thomaskioko.tvmaniac.util.paging.CommonPagingConfig
import com.thomaskioko.tvmaniac.util.paging.PaginatedRemoteMediator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.ExperimentalStoreApi
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadRequest.Companion.fresh
import org.mobilenativefoundation.store.store5.impl.extensions.fresh
import org.mobilenativefoundation.store.store5.impl.extensions.get
import kotlin.time.Duration.Companion.days

@Inject
class DefaultTrendingShowsRepository(
    private val store: TrendingShowsStore,
    private val requestManagerRepository: RequestManagerRepository,
    private val dao: TrendingShowsDao,
    private val dispatchers: AppCoroutineDispatchers,
) : TrendingShowsRepository {

    override suspend fun fetchTrendingShows(
        forceRefresh: Boolean,
    ): List<ShowEntity> {
        return when {
            forceRefresh -> store.stream(
                fresh(
                    key = TrendingShowsParams(
                        timeWindow = DEFAULT_DAY_TIME_WINDOW,
                        page = DEFAULT_API_PAGE,
                    ),
                ),
            )
                .filterForResult()
                .first()
                .dataOrNull() ?: getShows()

            else -> getShows()
        }
    }

    private suspend fun getShows(): List<ShowEntity> =
        store.get(
            key = TrendingShowsParams(
                timeWindow = DEFAULT_DAY_TIME_WINDOW,
                page = DEFAULT_API_PAGE,
            ),
        )

    override fun observeTrendingShows(): Flow<Either<Failure, List<ShowEntity>>> =
        store.stream(
            StoreReadRequest.cached(
                key = TrendingShowsParams(
                    timeWindow = DEFAULT_DAY_TIME_WINDOW,
                    page = DEFAULT_API_PAGE,
                ),
                refresh = requestManagerRepository.isRequestExpired(
                    entityId = DEFAULT_API_PAGE,
                    requestType = DEFAULT_DAY_TIME_WINDOW,
                    threshold = 1.days,
                ),
            ),
        )
            .mapResult()
            .flowOn(dispatchers.io)

    @OptIn(ExperimentalPagingApi::class, ExperimentalStoreApi::class)
    override fun getPagedTrendingShows(): Flow<PagingData<ShowEntity>> {
        return Pager(
            config = CommonPagingConfig.pagingConfig,
            remoteMediator = PaginatedRemoteMediator(
                getLastPage = dao::getLastPage,
                deleteLocalEntity = store::clear,
                fetch = {
                    store.fresh(
                        TrendingShowsParams(
                            timeWindow = DEFAULT_DAY_TIME_WINDOW,
                            page = it,
                        ),
                    )
                },
            ),
            pagingSourceFactory = dao::getPagedTrendingShows,
        ).flow
    }
}
