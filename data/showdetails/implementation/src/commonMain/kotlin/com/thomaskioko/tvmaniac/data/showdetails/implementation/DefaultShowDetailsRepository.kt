package com.thomaskioko.tvmaniac.data.showdetails.implementation

import com.thomaskioko.tvmaniac.core.db.TvshowDetails
import com.thomaskioko.tvmaniac.data.showdetails.api.ShowDetailsRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
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
class DefaultShowDetailsRepository(
    private val showStore: ShowDetailsStore,
    private val requestManagerRepository: RequestManagerRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : ShowDetailsRepository {

    override suspend fun getShowDetails(traktId: Long): TvshowDetails = showStore.get(key = traktId)

    override fun observeShowDetails(traktId: Long): Flow<Either<Failure, TvshowDetails>> =
        showStore.stream(
            StoreReadRequest.cached(
                key = traktId,
                refresh = requestManagerRepository.isRequestExpired(
                    entityId = traktId,
                    requestType = "TMDB_SHOW_DETAILS",
                    threshold = 6.days,
                ),
            ),
        )
            .mapResult()
            .flowOn(dispatchers.io)
}
