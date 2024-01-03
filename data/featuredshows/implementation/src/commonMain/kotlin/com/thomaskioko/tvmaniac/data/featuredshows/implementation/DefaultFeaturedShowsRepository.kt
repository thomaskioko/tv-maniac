package com.thomaskioko.tvmaniac.data.featuredshows.implementation

import com.thomaskioko.tvmaniac.data.featuredshows.api.FeaturedShowsRepository
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
class DefaultFeaturedShowsRepository(
    private val store: FeaturedShowsStore,
    private val requestManagerRepository: RequestManagerRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : FeaturedShowsRepository {

    override suspend fun fetchFeaturedTrendingShows(timeWindow: String): List<ShowEntity> =
        store.get(key = timeWindow)

    override fun observeFeaturedShows(
        timeWindow: String,
    ): Flow<Either<Failure, List<ShowEntity>>> =
        store.stream(
            StoreReadRequest.cached(
                key = timeWindow,
                refresh = requestManagerRepository.isRequestExpired(
                    entityId = Category.FEATURED.id,
                    requestType = timeWindow,
                    threshold = 1.days,
                ),
            ),
        )
            .mapResult()
            .flowOn(dispatchers.io)
}
