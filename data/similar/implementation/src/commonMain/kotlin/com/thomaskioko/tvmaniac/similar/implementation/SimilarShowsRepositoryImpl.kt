package com.thomaskioko.tvmaniac.similar.implementation

import com.thomaskioko.tvmaniac.core.db.SimilarShows
import com.thomaskioko.tvmaniac.core.networkutil.NetworkRepository
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse
import org.mobilenativefoundation.store.store5.impl.extensions.get

@Inject
class SimilarShowsRepositoryImpl(
    private val store: SimilarShowStore,
    private val networkRepository: NetworkRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : SimilarShowsRepository {

    override suspend fun fetchSimilarShows(traktId: Long): List<SimilarShows> = store.get(traktId)

    override fun observeSimilarShows(traktId: Long): Flow<StoreReadResponse<List<SimilarShows>>> =
        store.stream(StoreReadRequest.cached(key = traktId, refresh = networkRepository.isConnected()))
            .flowOn(dispatchers.io)
}
