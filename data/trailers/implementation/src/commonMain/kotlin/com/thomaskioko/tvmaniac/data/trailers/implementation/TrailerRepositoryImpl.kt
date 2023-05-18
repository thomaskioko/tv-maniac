package com.thomaskioko.tvmaniac.data.trailers.implementation

import com.thomaskioko.tvmaniac.core.db.Trailers
import com.thomaskioko.tvmaniac.util.AppUtils
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse
import org.mobilenativefoundation.store.store5.impl.extensions.get

@Inject
class TrailerRepositoryImpl(
    private val store: TrailerStore,
    private val appUtils: AppUtils,
    private val dispatchers: AppCoroutineDispatchers,
) : TrailerRepository {

    override fun isYoutubePlayerInstalled(): Flow<Boolean> = appUtils.isYoutubePlayerInstalled()

    override suspend fun fetchTrailersByShowId(traktId: Long): List<Trailers> =
        store.get(traktId)

    override fun observeTrailersStoreResponse(traktId: Long): Flow<StoreReadResponse<List<Trailers>>> =
        store.stream(StoreReadRequest.cached(key = traktId, refresh = true))
            .flowOn(dispatchers.io)
}
