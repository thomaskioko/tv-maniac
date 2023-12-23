package com.thomaskioko.tvmaniac.toprated.data.implementation

import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.shows.api.Category
import com.thomaskioko.tvmaniac.shows.api.ShowEntity
import com.thomaskioko.tvmaniac.tmdb.api.DEFAULT_API_PAGE
import com.thomaskioko.tvmaniac.topratedshows.data.api.TopRatedShowsRepository
import com.thomaskioko.tvmaniac.util.extensions.mapResult
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.impl.extensions.get
import kotlin.time.Duration.Companion.days

@Inject
class DefaultTopRatedShowsRepository(
    private val store: TopRatedShowsStore,
    private val requestManagerRepository: RequestManagerRepository,
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
                    requestType = Category.TOP_RATED.name,
                    threshold = 3.days,
                ),
            ),
        )
            .mapResult()
            .flowOn(dispatchers.io)
}
