package com.thomaskioko.tvmaniac.data.showdetails.implementation

import com.thomaskioko.tvmaniac.core.db.TvshowDetails
import com.thomaskioko.tvmaniac.data.showdetails.api.ShowDetailsRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.SHOW_DETAILS
import com.thomaskioko.tvmaniac.util.extensions.mapResult
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.impl.extensions.get

@Inject
class DefaultShowDetailsRepository(
    private val showStore: ShowDetailsStore,
    private val requestManagerRepository: RequestManagerRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : ShowDetailsRepository {

    override suspend fun getShowDetails(id: Long): TvshowDetails = showStore.get(key = id)

    override fun observeShowDetails(id: Long): Flow<Either<Failure, TvshowDetails>> =
        showStore.stream(
            StoreReadRequest.cached(
                key = id,
                refresh = requestManagerRepository.isRequestExpired(
                    entityId = id,
                    requestType = SHOW_DETAILS.name,
                    threshold = SHOW_DETAILS.duration,
                ),
            ),
        )
            .mapResult()
            .flowOn(dispatchers.io)
}
