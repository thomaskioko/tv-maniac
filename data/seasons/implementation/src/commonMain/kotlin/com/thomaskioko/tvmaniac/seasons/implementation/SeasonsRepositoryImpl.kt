package com.thomaskioko.tvmaniac.seasons.implementation

import com.thomaskioko.tvmaniac.core.db.Seasons
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse

@Inject
class SeasonsRepositoryImpl(
    private val seasonsStore: SeasonsStore,
    private val dispatcher: AppCoroutineDispatchers,
) : SeasonsRepository {

    override fun observeSeasonsStoreResponse(traktId: Long): Flow<StoreReadResponse<List<Seasons>>> =
        seasonsStore.stream(StoreReadRequest.cached(key = traktId, refresh = false))
            .flowOn(dispatcher.io)
}
