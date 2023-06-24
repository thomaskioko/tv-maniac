package com.thomaskioko.tvmaniac.seasons.implementation

import com.thomaskioko.tvmaniac.core.db.Seasons
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse
import org.mobilenativefoundation.store.store5.impl.extensions.get
import kotlin.time.Duration.Companion.days

@Inject
class SeasonsRepositoryImpl(
    private val seasonsStore: SeasonsStore,
    private val requestManagerRepository: RequestManagerRepository,
    private val dispatcher: AppCoroutineDispatchers,
) : SeasonsRepository {

    override suspend fun getSeasons(traktId: Long): List<Seasons> =
        seasonsStore.get(traktId)

    override fun observeSeasonsStoreResponse(traktId: Long): Flow<StoreReadResponse<List<Seasons>>> =
        seasonsStore.stream(
            StoreReadRequest.cached(
                key = traktId,
                refresh = requestManagerRepository.isRequestExpired(
                    entityId = traktId,
                    requestType = "SEASON",
                    threshold = 6.days,
                ),
            ),
        )
            .flowOn(dispatcher.io)
}
