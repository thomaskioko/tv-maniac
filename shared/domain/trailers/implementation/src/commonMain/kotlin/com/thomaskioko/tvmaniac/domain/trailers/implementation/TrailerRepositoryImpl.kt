package com.thomaskioko.tvmaniac.domain.trailers.implementation

import co.touchlab.kermit.Logger
import com.thomaskioko.tvmaniac.core.db.Trailers
import com.thomaskioko.tvmaniac.core.util.AppUtils
import com.thomaskioko.tvmaniac.core.util.network.ApiResponse
import com.thomaskioko.tvmaniac.core.util.network.Either
import com.thomaskioko.tvmaniac.core.util.network.Failure
import com.thomaskioko.tvmaniac.core.util.network.networkBoundResult
import com.thomaskioko.tvmaniac.domain.trailers.api.TrailerRepository
import com.thomaskioko.tvmaniac.domain.trailers.api.TrailerCache
import com.thomaskioko.tvmaniac.shows.api.cache.TvShowCache
import com.thomaskioko.tvmaniac.tmdb.api.TmdbService
import com.thomaskioko.tvmaniac.tmdb.api.model.ErrorResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.TrailersResponse
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

    override fun observeTrailersByShowId(traktId: Long): Flow<Either<Failure, List<Trailers>>> =
        networkBoundResult(
            query = { trailerCache.getTrailersByShowId(traktId) },
            shouldFetch = { it.isNullOrEmpty() },
            fetch = {
                val show = tvShowCache.getTvShow(traktId)
                apiService.getTrailers(show.tmdb_id!!)
            },
            saveFetchResult = { it.mapAndCache(traktId) },
            coroutineDispatcher = dispatcher
        )

    private fun ApiResponse<TrailersResponse, ErrorResponse>.mapAndCache(showId: Long) {
        when (this) {
            is ApiResponse.Error -> {
                Logger.withTag("mapResponse")
                    .e("$this")
            }

            is ApiResponse.Success -> {
                val cacheList = body.results.map { response ->
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
    }
}