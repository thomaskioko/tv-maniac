package com.thomaskioko.tvmaniac.similar.implementation

import co.touchlab.kermit.Logger
import com.thomaskioko.tvmaniac.datasource.cache.SelectSimilarShows
import com.thomaskioko.tvmaniac.remote.api.TvShowsService
import com.thomaskioko.tvmaniac.remote.api.model.TvShowsResponse
import com.thomaskioko.tvmaniac.shared.core.util.Resource
import com.thomaskioko.tvmaniac.shared.core.util.getErrorMessage
import com.thomaskioko.tvmaniac.shared.core.util.networkBoundResource
import com.thomaskioko.tvmaniac.showcommon.api.cache.TvShowCache
import com.thomaskioko.tvmaniac.similar.api.SimilarShowCache
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class SimilarShowsRepositoryImpl(
    private val apiService: TvShowsService,
    private val similarShowCache: SimilarShowCache,
    private val tvShowCache: TvShowCache,
    private val dispatcher: CoroutineDispatcher,
) : SimilarShowsRepository {

    override fun observeSimilarShows(showId: Long): Flow<Resource<List<SelectSimilarShows>>> =
        networkBoundResource(
            query = { similarShowCache.observeSimilarShows(showId) },
            shouldFetch = { it.isNullOrEmpty() },
            fetch = { apiService.getSimilarShows(showId) },
            saveFetchResult = { mapAndInsert(showId, it) },
            onFetchFailed = { Logger.withTag("observeSimilarShows").e { it.getErrorMessage() } },
            coroutineDispatcher = dispatcher
        )

    private fun mapAndInsert(showId: Long, response: TvShowsResponse) {
        response.results.forEach { show ->
            tvShowCache.insert(show.toShow())

            similarShowCache.insert(
                showId = showId,
                similarShowId = show.id.toLong()
            )
        }
    }
}
