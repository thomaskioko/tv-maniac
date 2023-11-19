package com.thomaskioko.tvmaniac.shows.implementation

import com.thomaskioko.tvmaniac.category.api.model.Category
import com.thomaskioko.tvmaniac.category.api.model.Category.ANTICIPATED
import com.thomaskioko.tvmaniac.category.api.model.Category.POPULAR
import com.thomaskioko.tvmaniac.category.api.model.Category.RECOMMENDED
import com.thomaskioko.tvmaniac.category.api.model.Category.TRENDING
import com.thomaskioko.tvmaniac.core.db.ShowById
import com.thomaskioko.tvmaniac.core.db.ShowsByCategory
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.shows.api.DiscoverRepository
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse
import org.mobilenativefoundation.store.store5.impl.extensions.get
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

@OptIn(ExperimentalCoroutinesApi::class)
@Inject
class DiscoverRepositoryImpl(
    private val showStore: ShowStore,
    private val discoverShowsStore: DiscoverShowsStore,
    private val requestManagerRepository: RequestManagerRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : DiscoverRepository {

    override suspend fun getShowById(traktId: Long): ShowById = showStore.get(key = traktId)

    override fun observeShow(traktId: Long): Flow<Either<Failure, ShowById>> = showStore.stream(
        StoreReadRequest.cached(
            key = traktId,
            refresh = requestManagerRepository.isRequestExpired(
                entityId = traktId,
                requestType = "SHOW_DETAILS",
                threshold = 6.days,
            ),
        ),
    )
        .mapResult()

    override suspend fun fetchShows(category: Category): List<ShowsByCategory> =
        discoverShowsStore.get(key = category)

    override fun observeShowCategory(
        category: Category,
        duration: Duration,
    ): Flow<Either<Failure, List<ShowsByCategory>>> = discoverShowsStore.stream(
        StoreReadRequest.cached(
            key = category,
            refresh = requestManagerRepository.isRequestExpired(
                entityId = category.id,
                requestType = category.title,
                threshold = duration,
            ),
        ),
    )
        .mapResult()

    override suspend fun fetchDiscoverShows() {
        val categories = listOf(TRENDING, POPULAR, ANTICIPATED, RECOMMENDED)

        for (category in categories) {
            discoverShowsStore.get(category)
        }
    }

    private fun <T> Flow<StoreReadResponse<T>>.mapResult(): Flow<Either<Failure, T>> =
        distinctUntilChanged()
            .flatMapLatest {
                val data = it.dataOrNull()
                if (data != null) {
                    flowOf(Either.Right(data))
                } else {
                    emptyFlow()
                }
            }
            .flowOn(dispatchers.io)
}
