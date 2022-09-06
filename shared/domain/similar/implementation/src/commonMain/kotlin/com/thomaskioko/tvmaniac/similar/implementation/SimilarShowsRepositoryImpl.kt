package com.thomaskioko.tvmaniac.similar.implementation

import co.touchlab.kermit.Logger
import com.thomaskioko.tvmaniac.core.db.SelectSimilarShows
import com.thomaskioko.tvmaniac.core.util.ExceptionHandler.resolveError
import com.thomaskioko.tvmaniac.core.util.network.Resource
import com.thomaskioko.tvmaniac.core.util.network.networkBoundResource
import com.thomaskioko.tvmaniac.showcommon.api.cache.TvShowCache
import com.thomaskioko.tvmaniac.similar.api.SimilarShowCache
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import com.thomaskioko.tvmaniac.trakt.api.TraktService
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class SimilarShowsRepositoryImpl(
    private val traktService: TraktService,
    private val similarShowCache: SimilarShowCache,
    private val tvShowCache: TvShowCache,
    private val dispatcher: CoroutineDispatcher,
) : SimilarShowsRepository {

    override fun observeSimilarShows(traktId: Int): Flow<Resource<List<SelectSimilarShows>>> =
        networkBoundResource(
            query = { similarShowCache.observeSimilarShows(traktId) },
            shouldFetch = { it.isNullOrEmpty() },
            fetch = { traktService.getSimilarShows(traktId) },
            saveFetchResult = { response -> mapAndInsert(traktId, response) },
            onFetchFailed = { Logger.withTag("observeSimilarShows").e { it.resolveError() } },
            coroutineDispatcher = dispatcher
        )

    private fun mapAndInsert(traktId: Int, traktShowsResponses: List<TraktShowResponse>) {
        traktShowsResponses.forEach { showsResponse ->
            tvShowCache.insert(showsResponse.toShow())

            similarShowCache.insert(
                traktId = traktId,
                similarShowId = showsResponse.ids.trakt
            )
        }
    }
}
