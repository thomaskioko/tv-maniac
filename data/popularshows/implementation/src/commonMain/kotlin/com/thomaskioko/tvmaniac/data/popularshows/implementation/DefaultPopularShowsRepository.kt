package com.thomaskioko.tvmaniac.data.popularshows.implementation

import androidx.paging.ExperimentalPagingApi
import app.cash.paging.Pager
import app.cash.paging.PagingData
import com.thomaskioko.tvmaniac.data.popularshows.api.PopularShowsDao
import com.thomaskioko.tvmaniac.data.popularshows.api.PopularShowsRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.shows.api.Category
import com.thomaskioko.tvmaniac.shows.api.ShowEntity
import com.thomaskioko.tvmaniac.tmdb.api.DEFAULT_API_PAGE
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
import kotlin.time.Duration.Companion.days

@Inject
class DefaultPopularShowsRepository(
    private val store: PopularShowsStore,
    private val popularShowsDao: PopularShowsDao,
    private val requestManagerRepository: RequestManagerRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : PopularShowsRepository {

    override suspend fun fetchPopularShows(): List<ShowEntity> =
        store.get(key = DEFAULT_API_PAGE)

    override fun observePopularShows(): Flow<Either<Failure, List<ShowEntity>>> =
        store.stream(
            StoreReadRequest.cached(
                key = DEFAULT_API_PAGE,
                refresh = requestManagerRepository.isRequestExpired(
                    entityId = DEFAULT_API_PAGE,
                    requestType = Category.POPULAR.name,
                    threshold = 3.days,
                ),
            ),
        )
            .mapResult()
            .flowOn(dispatchers.io)

    @OptIn(ExperimentalPagingApi::class, ExperimentalStoreApi::class)
    override fun getPagedPopularShows(): Flow<PagingData<ShowEntity>> {
        return Pager(
            config = pagingConfig,
            remoteMediator = PaginatedRemoteMediator(
                getLastPage = popularShowsDao::getLastPage,
                deleteLocalEntity = store::clear,
                fetch = store::fresh,
            ),
            pagingSourceFactory = popularShowsDao::getPagedPopularShows,
        ).flow
    }
}
