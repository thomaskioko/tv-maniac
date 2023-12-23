package com.thomaskioko.tvmaniac.discover.implementation

import com.thomaskioko.tvmaniac.discover.api.TrendingShowsRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.shows.api.Category
import com.thomaskioko.tvmaniac.shows.api.ShowEntity
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
class DefaultTrendingShowsRepository(
    private val trendingShowsStore: TrendingShowsStore,
    private val requestManagerRepository: RequestManagerRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : TrendingShowsRepository {

    override fun observeTrendingShows(timeWindow: String): Flow<Either<Failure, List<ShowEntity>>> =
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

    override suspend fun fetchTrendingShows(timeWindow: String): List<ShowEntity> =
        trendingShowsStore.get(key = timeWindow)
}
