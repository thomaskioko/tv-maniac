package com.thomaskioko.tvmaniac.shows.implementation

import com.thomaskioko.tvmaniac.core.db.ShowById
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.shows.api.DiscoverRepository
import com.thomaskioko.tvmaniac.util.extensions.mapResult
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.impl.extensions.get
import kotlin.time.Duration.Companion.days

@Inject
class DiscoverRepositoryImpl(
    private val showStore: ShowStore,
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
        .flowOn(dispatchers.io)

}
