package com.thomaskioko.tvmaniac.similar.implementation

import com.thomaskioko.tvmaniac.core.db.SimilarShows
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import com.thomaskioko.tvmaniac.tmdb.api.DEFAULT_API_PAGE
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
class SimilarShowsRepositoryImpl(
    private val store: SimilarShowStore,
    private val requestManagerRepository: RequestManagerRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : SimilarShowsRepository {

    override suspend fun fetchSimilarShows(traktId: Long): List<SimilarShows> =
        store.get(SimilarParams(showId = traktId, page = DEFAULT_API_PAGE))

    override fun observeSimilarShows(traktId: Long): Flow<Either<Failure, List<SimilarShows>>> =
        store.stream(
            StoreReadRequest.cached(
                key = SimilarParams(showId = traktId, page = DEFAULT_API_PAGE),
                refresh = requestManagerRepository.isRequestExpired(
                    entityId = traktId,
                    requestType = "SIMILAR_SHOWS",
                    threshold = 6.days,
                ),
            ),
        )
            .mapResult()
            .flowOn(dispatchers.io)
}
