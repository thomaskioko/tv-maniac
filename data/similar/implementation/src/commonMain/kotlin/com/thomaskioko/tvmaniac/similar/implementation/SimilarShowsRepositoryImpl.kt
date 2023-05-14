package com.thomaskioko.tvmaniac.similar.implementation

import com.thomaskioko.tvmaniac.core.db.SelectSimilarShows
import com.thomaskioko.tvmaniac.core.networkutil.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.core.networkutil.Failure
import com.thomaskioko.tvmaniac.core.networkutil.networkBoundResult
import com.thomaskioko.tvmaniac.shows.api.cache.ShowsDao
import com.thomaskioko.tvmaniac.similar.api.SimilarShowCache
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import com.thomaskioko.tvmaniac.trakt.api.TraktService
import com.thomaskioko.tvmaniac.trakt.api.model.ErrorResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowResponse
import com.thomaskioko.tvmaniac.util.ExceptionHandler
import com.thomaskioko.tvmaniac.util.KermitLogger
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class SimilarShowsRepositoryImpl(
    private val traktService: TraktService,
    private val similarShowCache: SimilarShowCache,
    private val showsDao: ShowsDao,
    private val exceptionHandler: ExceptionHandler,
    private val dispatchers: AppCoroutineDispatchers,
    private val logger: KermitLogger,
) : SimilarShowsRepository {

    override fun observeSimilarShows(traktId: Long): Flow<Either<Failure, List<SelectSimilarShows>>> =
        networkBoundResult(
            query = { similarShowCache.observeSimilarShows(traktId) },
            shouldFetch = { it.isNullOrEmpty() },
            fetch = { traktService.getSimilarShows(traktId) },
            saveFetchResult = { response -> mapAndInsert(traktId, response) },
            exceptionHandler = exceptionHandler,
            coroutineDispatcher = dispatchers.io,
        )

    private fun mapAndInsert(
        traktId: Long,
        response: ApiResponse<List<TraktShowResponse>, ErrorResponse>,
    ) {
        when (response) {
            is ApiResponse.Success -> {
                response.body.forEach { showsResponse ->
                    showsDao.insert(showsResponse.toShow())

                    similarShowCache.insert(
                        traktId = traktId,
                        similarShowId = showsResponse.ids.trakt.toLong(),
                    )
                }
            }

            is ApiResponse.Error.GenericError -> {
                logger.error("observeSimilarShows", "$response")
                throw Throwable("${response.errorMessage}")
            }

            is ApiResponse.Error.HttpError -> {
                logger.error("observeSimilarShows", "$response")
                throw Throwable("${response.code} - ${response.errorBody?.message}")
            }

            is ApiResponse.Error.SerializationError -> {
                logger.error("observeSimilarShows", "$response")
                throw Throwable("$response")
            }
        }
    }
}
