package com.thomaskioko.tvmaniac.shows.implementation

import com.thomaskioko.tvmaniac.category.api.model.Category
import com.thomaskioko.tvmaniac.category.api.model.Category.ANTICIPATED
import com.thomaskioko.tvmaniac.category.api.model.Category.POPULAR
import com.thomaskioko.tvmaniac.category.api.model.Category.RECOMMENDED
import com.thomaskioko.tvmaniac.category.api.model.Category.TRENDING
import com.thomaskioko.tvmaniac.core.db.ShowById
import com.thomaskioko.tvmaniac.core.db.ShowsByCategory
import com.thomaskioko.tvmaniac.core.networkutil.NetworkRepository
import com.thomaskioko.tvmaniac.shows.api.ShowsRepository
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse
import org.mobilenativefoundation.store.store5.impl.extensions.get

@Inject
class ShowsRepositoryImpl constructor(
    private val showStore: ShowStore,
    private val discoverShowsStore: DiscoverShowsStore,
    private val networkRepository: NetworkRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : ShowsRepository {

    override suspend fun fetchShow(traktId: Long): ShowById =
        showStore.get(key = traktId)

    override fun observeShow(traktId: Long): Flow<StoreReadResponse<ShowById>> =
        showStore.stream(StoreReadRequest.cached(key = traktId, refresh = networkRepository.isConnected()))
            .flowOn(dispatchers.io)

    override suspend fun fetchShows(category: Category): List<ShowsByCategory> =
        discoverShowsStore.get(key = category)

    override fun observeRecommendedShows(categoryId: Long): Flow<StoreReadResponse<List<ShowsByCategory>>> =
        discoverShowsStore.stream(StoreReadRequest.cached(key = RECOMMENDED, refresh = networkRepository.isConnected()))
            .flowOn(dispatchers.io)

    override fun observeTrendingShows(): Flow<StoreReadResponse<List<ShowsByCategory>>> =
        discoverShowsStore.stream(StoreReadRequest.cached(key = TRENDING, refresh = networkRepository.isConnected()))
            .flowOn(dispatchers.io)

    override fun observePopularShows(): Flow<StoreReadResponse<List<ShowsByCategory>>> =
        discoverShowsStore.stream(StoreReadRequest.cached(key = POPULAR, refresh = networkRepository.isConnected()))
            .flowOn(dispatchers.io)

    override fun observeAnticipatedShows(): Flow<StoreReadResponse<List<ShowsByCategory>>> =
        discoverShowsStore.stream(StoreReadRequest.cached(key = ANTICIPATED, refresh = networkRepository.isConnected()))
            .flowOn(dispatchers.io)

    override fun observeFeaturedShows(): Flow<StoreReadResponse<List<ShowsByCategory>>> =
        discoverShowsStore.stream(StoreReadRequest.cached(key = RECOMMENDED, refresh = networkRepository.isConnected()))
            .flowOn(dispatchers.io)

    override suspend fun fetchDiscoverShows() {
        val categories = listOf(TRENDING, POPULAR, ANTICIPATED, RECOMMENDED)

        for (category in categories) {
            discoverShowsStore.get(category)
        }
    }
}
