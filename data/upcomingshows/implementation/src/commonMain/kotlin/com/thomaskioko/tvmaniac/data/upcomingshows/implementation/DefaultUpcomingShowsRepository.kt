package com.thomaskioko.tvmaniac.data.upcomingshows.implementation

import com.thomaskioko.tvmaniac.category.api.model.Category
import com.thomaskioko.tvmaniac.core.db.UpcomingShows
import com.thomaskioko.tvmaniac.data.upcomingshows.api.UpcomingShowsRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.tmdb.api.DEFAULT_API_PAGE
import com.thomaskioko.tvmaniac.tmdb.api.DEFAULT_SORT_ORDER
import com.thomaskioko.tvmaniac.util.PlatformDateFormatter
import com.thomaskioko.tvmaniac.util.extensions.mapResult
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import com.thomaskioko.tvmaniac.util.startOfDay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.impl.extensions.get
import kotlin.time.Duration.Companion.days

@Inject
class DefaultUpcomingShowsRepository(
    dateFormatter: PlatformDateFormatter,
    private val store: UpcomingShowsStore,
    private val requestManagerRepository: RequestManagerRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : UpcomingShowsRepository {

    // TODO:: Load this from duration repository. Default range is 4 months
    private val params = UpcomingParams(
        startDate = dateFormatter.formatDate(startOfDay.toEpochMilliseconds()),
        endDate = dateFormatter.formatDate(startOfDay.plus(122.days).toEpochMilliseconds()),
        page = DEFAULT_API_PAGE,
    )

    override fun observeUpcomingShows(): Flow<Either<Failure, List<UpcomingShows>>> =
        store.stream(
            StoreReadRequest.cached(
                key = params,
                refresh = requestManagerRepository.isRequestExpired(
                    entityId = params.page,
                    requestType = Category.UPCOMING.name,
                    threshold = 3.days,
                ),
            ),
        )
            .mapResult()
            .flowOn(dispatchers.io)

    override suspend fun fetchUpcomingShows(): List<UpcomingShows> =
        store.get(key = params)
}

data class UpcomingParams(
    val startDate: String,
    val endDate: String,
    val page: Long,
    val sortBy: String = DEFAULT_SORT_ORDER,
)
