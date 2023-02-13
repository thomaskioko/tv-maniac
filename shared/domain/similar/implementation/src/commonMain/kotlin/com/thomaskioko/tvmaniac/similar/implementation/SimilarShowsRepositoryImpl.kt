package com.thomaskioko.tvmaniac.similar.implementation

import co.touchlab.kermit.Logger
import com.thomaskioko.tvmaniac.core.db.SelectSimilarShows
import com.thomaskioko.tvmaniac.core.util.network.ApiResponse
import com.thomaskioko.tvmaniac.core.util.network.Either
import com.thomaskioko.tvmaniac.core.util.network.Failure
import com.thomaskioko.tvmaniac.core.util.network.networkBoundResult
import com.thomaskioko.tvmaniac.shows.api.cache.TvShowCache
import com.thomaskioko.tvmaniac.similar.api.SimilarShowCache
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import com.thomaskioko.tvmaniac.trakt.api.TraktService
import com.thomaskioko.tvmaniac.trakt.api.model.ErrorResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class SimilarShowsRepositoryImpl(
    private val traktService: TraktService,
    private val similarShowCache: SimilarShowCache,
    private val tvShowCache: TvShowCache,
    private val dispatcher: CoroutineDispatcher,
) : SimilarShowsRepository {

    override fun observeSimilarShows(traktId: Long): Flow<Either<Failure, List<SelectSimilarShows>>> =
        networkBoundResult(
            query = { similarShowCache.observeSimilarShows(traktId) },
            shouldFetch = { it.isNullOrEmpty() },
            fetch = { traktService.getSimilarShows(traktId) },
            saveFetchResult = { response -> mapAndInsert(traktId, response) },
            coroutineDispatcher = dispatcher
        )

    private fun mapAndInsert(traktId: Long, response: ApiResponse<List<TraktShowResponse>, ErrorResponse>) {
        when (response) {
            is ApiResponse.Error -> {
                Logger.withTag("observeSimilarShows")
                    .e("$response")
            }

            is ApiResponse.Success -> {
                response.body.forEach { showsResponse ->
                    tvShowCache.insert(showsResponse.toShow())

                    similarShowCache.insert(
                        traktId = traktId,
                        similarShowId = showsResponse.ids.trakt.toLong()
                    )
                }
            }
        }
    }
}
