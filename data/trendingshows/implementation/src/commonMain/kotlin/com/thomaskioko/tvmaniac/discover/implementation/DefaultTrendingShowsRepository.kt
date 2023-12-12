package com.thomaskioko.tvmaniac.discover.implementation

import com.thomaskioko.tvmaniac.category.api.model.Category
import com.thomaskioko.tvmaniac.core.db.TrendingShows
import com.thomaskioko.tvmaniac.discover.api.TrendingShowsRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.util.extensions.mapResult
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.impl.extensions.get
import kotlin.time.Duration.Companion.days

@OptIn(ExperimentalCoroutinesApi::class)
@Inject
class DefaultTrendingShowsRepository(
    private val trendingShowsStore: TrendingShowsStore,
    private val requestManagerRepository: RequestManagerRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : TrendingShowsRepository {

    override fun observeTrendingShows(timeWindow: String): Flow<Either<Failure, List<TrendingShows>>> =
        trendingShowsStore.stream(
            StoreReadRequest.cached(
                key = timeWindow,
                refresh = requestManagerRepository.isRequestExpired(
                    entityId = Category.TRENDING_TODAY.id,
                    requestType = Category.TRENDING_TODAY.name,
                    threshold = 1.days,
                ),
            ),
        )
            .mapResult()
            .flowOn(dispatchers.io)

    override fun observeFeaturedTrendingShows(timeWindow: String): Flow<Either<Failure, List<TrendingShows>>> =
        observeTrendingShows(timeWindow)
            .flatMapLatest {
                it.fold({
                    emptyFlow()
                }, { shows ->
                    shows?.let { trendingShows ->
                        flowOf(Either.Right(trendingShows.sortedByDescending { it.popularity }.take(5)))
                    } ?: emptyFlow()
                })
            }
            .flowOn(dispatchers.io)

    override suspend fun fetchTrendingShows(timeWindow: String): List<TrendingShows> =
        trendingShowsStore.get(key = timeWindow)

    override suspend fun fetchFeaturedTrendingShows(timeWindow: String): List<TrendingShows> =
        fetchTrendingShows(timeWindow)
            .sortedBy { it.popularity }
            .take(5)
}
