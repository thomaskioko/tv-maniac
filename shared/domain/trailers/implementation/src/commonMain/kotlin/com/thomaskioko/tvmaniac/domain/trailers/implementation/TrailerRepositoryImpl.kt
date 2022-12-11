package com.thomaskioko.tvmaniac.domain.trailers.implementation

import co.touchlab.kermit.Logger
import com.thomaskioko.tvmaniac.core.db.Trailers
import com.thomaskioko.tvmaniac.core.util.ExceptionHandler.resolveError
import com.thomaskioko.tvmaniac.core.util.AppUtils
import com.thomaskioko.tvmaniac.core.util.network.Resource
import com.thomaskioko.tvmaniac.core.util.network.networkBoundResource
import com.thomaskioko.tvmaniac.tmdb.api.model.TrailersResponse
import com.thomaskioko.tvmaniac.shared.domain.trailers.api.TrailerCache
import com.thomaskioko.tvmaniac.domain.trailers.api.TrailerRepository
import com.thomaskioko.tvmaniac.shows.api.cache.TvShowCache
import com.thomaskioko.tvmaniac.tmdb.api.TmdbService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class TrailerRepositoryImpl(
    private val apiService: TmdbService,
    private val trailerCache: TrailerCache,
    private val tvShowCache: TvShowCache,
    private val appUtils: AppUtils,
    private val dispatcher: CoroutineDispatcher,
) : TrailerRepository {

    override fun isWebViewInstalled(): Flow<Boolean> = appUtils.isYoutubePlayerInstalled()

    override fun observeTrailersByShowId(traktId: Int): Flow<Resource<List<Trailers>>> =
        networkBoundResource(
            query = { trailerCache.getTrailersByShowId(traktId) },
            shouldFetch = { it.isNullOrEmpty() },
            fetch = {
                tvShowCache.getTvShow(traktId)?.let {
                    it.tmdb_id?.let { tmdbId ->
                        apiService.getTrailers(tmdbId)
                    }
                }
            },
            saveFetchResult = { it?.mapAndCache(traktId) },
            onFetchFailed = { Logger.withTag("observeTrailersByShowId").e(it.resolveError()) },
            coroutineDispatcher = dispatcher
        )

    private fun TrailersResponse.mapAndCache(showId: Int) {
        val cacheList = results.map { response ->
            Trailers(
                id = response.id,
                trakt_id = showId,
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