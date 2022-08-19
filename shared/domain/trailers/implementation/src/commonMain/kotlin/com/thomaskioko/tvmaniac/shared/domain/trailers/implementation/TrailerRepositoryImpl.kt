package com.thomaskioko.tvmaniac.shared.domain.trailers.implementation

import co.touchlab.kermit.Logger
import com.thomaskioko.tvmaniac.core.db.Trailers
import com.thomaskioko.tvmaniac.core.util.ExceptionHandler.resolveError
import com.thomaskioko.tvmaniac.core.util.network.Resource
import com.thomaskioko.tvmaniac.core.util.network.networkBoundResource
import com.thomaskioko.tvmaniac.tmdb.api.model.TrailersResponse
import com.thomaskioko.tvmaniac.shared.domain.trailers.api.TrailerCache
import com.thomaskioko.tvmaniac.shared.domain.trailers.api.TrailerRepository
import com.thomaskioko.tvmaniac.tmdb.api.TmdbService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class TrailerRepositoryImpl(
    private val apiService: TmdbService,
    private val trailerCache: TrailerCache,
    private val dispatcher: CoroutineDispatcher,
) : TrailerRepository {

    override fun observeTrailersByShowId(showId: Long): Flow<Resource<List<Trailers>>> =
        networkBoundResource(
            query = { trailerCache.getTrailersByShowId(showId) },
            shouldFetch = { it.isNullOrEmpty() },
            fetch = { apiService.getTrailers(showId) },
            saveFetchResult = { it.mapAndCache(showId) },
            onFetchFailed = { Logger.withTag("observeTrailersByShowId").e(it.resolveError()) },
            coroutineDispatcher = dispatcher
        )

    private fun TrailersResponse.mapAndCache(showId: Long) {
        val cacheList = results.map { response ->
            Trailers(
                id = response.id,
                show_id = showId,
                key = response.key,
                name = response.name,
                site = response.site,
                size = response.size.toLong(),
                type = response.type
            )
        }
        trailerCache.insert(cacheList)
    }
}