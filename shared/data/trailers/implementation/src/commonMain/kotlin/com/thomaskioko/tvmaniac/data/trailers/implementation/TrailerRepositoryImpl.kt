package com.thomaskioko.tvmaniac.data.trailers.implementation

import co.touchlab.kermit.Logger
import com.thomaskioko.tvmaniac.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.base.util.AppUtils
import com.thomaskioko.tvmaniac.base.util.ExceptionHandler
import com.thomaskioko.tvmaniac.core.db.Trailers
import com.thomaskioko.tvmaniac.core.networkutil.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.core.networkutil.Failure
import com.thomaskioko.tvmaniac.core.networkutil.networkBoundResult
import com.thomaskioko.tvmaniac.shows.api.cache.ShowsCache
import com.thomaskioko.tvmaniac.tmdb.api.TmdbService
import com.thomaskioko.tvmaniac.tmdb.api.model.ErrorResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.TrailersResponse
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class TrailerRepositoryImpl(
    private val apiService: TmdbService,
    private val trailerCache: TrailerCache,
    private val showsCache: ShowsCache,
    private val appUtils: AppUtils,
    private val exceptionHandler: ExceptionHandler,
    private val dispatchers: AppCoroutineDispatchers,
) : TrailerRepository {

    override fun isYoutubePlayerInstalled(): Flow<Boolean> = appUtils.isYoutubePlayerInstalled()

    override fun observeTrailersByShowId(traktId: Long): Flow<Either<Failure, List<Trailers>>> =
        networkBoundResult(
            query = { trailerCache.getTrailersByShowId(traktId) },
            shouldFetch = { it.isNullOrEmpty() },
            fetch = {
                val show = showsCache.getTvShow(traktId)
                apiService.getTrailers(show.tmdb_id!!)
            },
            saveFetchResult = { it.mapAndCache(traktId) },
            exceptionHandler = exceptionHandler,
            coroutineDispatcher = dispatchers.io
        )

    private fun ApiResponse<TrailersResponse, ErrorResponse>.mapAndCache(showId: Long) {
        when (this) {
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

            is ApiResponse.Error.GenericError -> {
                Logger.withTag("observeTrailersByShowId").e("$this")
                throw Throwable("$errorMessage")
            }

            is ApiResponse.Error.HttpError -> {
                Logger.withTag("observeTrailersByShowId").e("$this")
                throw Throwable("$code - ${errorBody?.message}")
            }

            is ApiResponse.Error.SerializationError -> {
                Logger.withTag("observeTrailersByShowId").e("$this")
                throw Throwable("$this")
            }
        }
    }
}