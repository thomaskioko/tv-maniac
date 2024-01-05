package com.thomaskioko.tvmaniac.toprated.data.implementation

import androidx.paging.ExperimentalPagingApi
import app.cash.paging.Pager
import app.cash.paging.PagingData
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.TOP_RATED_SHOWS
import com.thomaskioko.tvmaniac.shows.api.ShowEntity
import com.thomaskioko.tvmaniac.tmdb.api.DEFAULT_API_PAGE
import com.thomaskioko.tvmaniac.topratedshows.data.api.TopRatedShowsDao
import com.thomaskioko.tvmaniac.topratedshows.data.api.TopRatedShowsRepository
import com.thomaskioko.tvmaniac.util.extensions.mapResult
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import com.thomaskioko.tvmaniac.util.paging.CommonPagingConfig.pagingConfig
import com.thomaskioko.tvmaniac.util.paging.PaginatedRemoteMediator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.ExperimentalStoreApi
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

    override suspend fun fetchTopRatedShows(): List<ShowEntity> =
        store.get(key = DEFAULT_API_PAGE)

    override fun observeTopRatedShows(): Flow<Either<Failure, List<ShowEntity>>> =
        store.stream(
            StoreReadRequest.cached(
                key = DEFAULT_API_PAGE,
                refresh = requestManagerRepository.isRequestExpired(
                    entityId = DEFAULT_API_PAGE,
                    requestType = TOP_RATED_SHOWS.name,
                    threshold = TOP_RATED_SHOWS.duration,
                ),
            ),
        )
            .mapResult()
            .flowOn(dispatchers.io)

    @OptIn(ExperimentalPagingApi::class, ExperimentalStoreApi::class)
    override fun getPagedTopRatedShows(): Flow<PagingData<ShowEntity>> {
        return Pager(
            config = pagingConfig,
            remoteMediator = PaginatedRemoteMediator(
                getLastPage = dao::getLastPage,
                deleteLocalEntity = store::clear,
                fetch = store::fresh,
            ),
            pagingSourceFactory = dao::getPagedTopRatedShows,
        ).flow
    }
}
