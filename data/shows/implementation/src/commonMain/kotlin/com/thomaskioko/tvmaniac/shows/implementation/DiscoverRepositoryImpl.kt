package com.thomaskioko.tvmaniac.shows.implementation

import com.thomaskioko.tvmaniac.category.api.model.Category
import com.thomaskioko.tvmaniac.category.api.model.Category.ANTICIPATED
import com.thomaskioko.tvmaniac.category.api.model.Category.POPULAR
import com.thomaskioko.tvmaniac.category.api.model.Category.RECOMMENDED
import com.thomaskioko.tvmaniac.category.api.model.Category.TRENDING
import com.thomaskioko.tvmaniac.category.api.model.getCategory
import com.thomaskioko.tvmaniac.core.db.ShowById
import com.thomaskioko.tvmaniac.core.db.ShowsByCategory
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.shows.api.DiscoverRepository
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse
import org.mobilenativefoundation.store.store5.impl.extensions.get
import kotlin.time.Duration.Companion.days

@Inject
class DiscoverRepositoryImpl constructor(
    private val showStore: ShowStore,
    private val discoverShowsStore: DiscoverShowsStore,
    private val requestManagerRepository: RequestManagerRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : DiscoverRepository {

    override suspend fun getShowById(traktId: Long): ShowById =
        showStore.get(key = traktId)

    override fun observeShow(traktId: Long): Flow<StoreReadResponse<ShowById>> =
        showStore.stream(
            StoreReadRequest.cached(
                key = traktId,
                refresh = requestManagerRepository.isRequestExpired(
                    entityId = traktId,
                    requestType = "SHOW_DETAILS",
                    threshold = 6.days,
                ),
            ),
        )
            .flowOn(dispatchers.io)

    override suspend fun fetchShows(category: Category): List<ShowsByCategory> =
        discoverShowsStore.get(key = category)

    override fun observeShowsByCategory(categoryId: Long): Flow<StoreReadResponse<List<ShowsByCategory>>> =
        discoverShowsStore.stream(
            StoreReadRequest.cached(
                key = categoryId.getCategory(),
                refresh = requestManagerRepository.isRequestExpired(
                    entityId = categoryId,
                    requestType = categoryId.getCategory().title,
                    threshold = 3.days,
                ),
            ),
        )
            .flowOn(dispatchers.io)

    override fun observeTrendingShows(): Flow<StoreReadResponse<List<ShowsByCategory>>> =
        discoverShowsStore.stream(
            StoreReadRequest.cached(
                key = TRENDING,
                refresh = requestManagerRepository.isRequestExpired(
                    entityId = TRENDING.id,
                    requestType = TRENDING.title,
                    threshold = 3.days,
                ),
            ),
        )
            .flowOn(dispatchers.io)

    override fun observePopularShows(): Flow<StoreReadResponse<List<ShowsByCategory>>> =
        discoverShowsStore.stream(
            StoreReadRequest.cached(
                key = POPULAR,
                refresh = requestManagerRepository.isRequestExpired(
                    entityId = POPULAR.id,
                    requestType = POPULAR.title,
                    threshold = 3.days,
                ),
            ),
        )
            .flowOn(dispatchers.io)

    override fun observeAnticipatedShows(): Flow<StoreReadResponse<List<ShowsByCategory>>> =
        discoverShowsStore.stream(
            StoreReadRequest.cached(
                key = ANTICIPATED,
                refresh = requestManagerRepository.isRequestExpired(
                    entityId = ANTICIPATED.id,
                    requestType = ANTICIPATED.title,
                    threshold = 3.days,
                ),
            ),
        )
            .flowOn(dispatchers.io)

    override fun observeRecommendedShows(): Flow<StoreReadResponse<List<ShowsByCategory>>> =
        discoverShowsStore.stream(
            StoreReadRequest.cached(
                key = RECOMMENDED,
                refresh = requestManagerRepository.isRequestExpired(
                    entityId = RECOMMENDED.id,
                    requestType = RECOMMENDED.title,
                    threshold = 1.days,
                ),
            ),
        )
            .flowOn(dispatchers.io)

    override suspend fun fetchDiscoverShows() {
        val categories = listOf(TRENDING, POPULAR, ANTICIPATED, RECOMMENDED)

        for (category in categories) {
            discoverShowsStore.get(category)
        }
    }
}
