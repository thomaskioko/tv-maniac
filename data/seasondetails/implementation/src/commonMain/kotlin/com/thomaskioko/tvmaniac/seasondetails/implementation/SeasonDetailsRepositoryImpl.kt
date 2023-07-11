package com.thomaskioko.tvmaniac.seasondetails.implementation

import com.thomaskioko.tvmaniac.core.db.SeasonWithEpisodes
import com.thomaskioko.tvmaniac.core.db.Season_episodes
import com.thomaskioko.tvmaniac.core.networkutil.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.DefaultError
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.core.networkutil.Failure
import com.thomaskioko.tvmaniac.core.networkutil.NetworkExceptionHandler
import com.thomaskioko.tvmaniac.core.networkutil.networkBoundResult
import com.thomaskioko.tvmaniac.episodes.api.EpisodesDao
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsDao
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.trakt.api.TraktShowsRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.ErrorResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSeasonEpisodesResponse
import com.thomaskioko.tvmaniac.util.KermitLogger
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject

@Inject
class SeasonDetailsRepositoryImpl(
    private val remoteDataSource: TraktShowsRemoteDataSource,
    private val seasonCache: SeasonDetailsDao,
    private val episodesDao: EpisodesDao,
    private val exceptionHandler: NetworkExceptionHandler,
    private val dispatcher: AppCoroutineDispatchers,
    private val logger: KermitLogger,
) : SeasonDetailsRepository {

    override fun observeSeasonDetailsStream(traktId: Long): Flow<Either<Failure, List<SeasonWithEpisodes>>> =
        networkBoundResult(
            query = { seasonCache.observeShowEpisodes(traktId) },
            shouldFetch = { it.isNullOrEmpty() },
            fetch = { remoteDataSource.getSeasonEpisodes(traktId) },
            saveFetchResult = { mapResponse(traktId, it) },
            exceptionHandler = exceptionHandler,
            coroutineDispatcher = dispatcher.io,
        )

    override fun observeSeasonDetails(traktId: Long): Flow<Either<Failure, List<SeasonWithEpisodes>>> =
        seasonCache.observeShowEpisodes(traktId)
            .catch { Either.Left(DefaultError(exceptionHandler.resolveError(it))) }
            .map { Either.Right(it) }

    private fun mapResponse(
        showId: Long,
        response: ApiResponse<List<TraktSeasonEpisodesResponse>, ErrorResponse>,
    ) {
        when (response) {
            is ApiResponse.Success -> {
                response.body.forEach { season ->
                    episodesDao.insert(season.toEpisodeCacheList())

                    seasonCache.insert(
                        Season_episodes(
                            show_id = showId,
                            season_id = season.ids.trakt.toLong(),
                            season_number = season.number.toLong(),
                        ),
                    )
                }
            }

            is ApiResponse.Error.GenericError -> {
                logger.error("observeSeasonDetails", "$response")
                throw Throwable("${response.errorMessage}")
            }

            is ApiResponse.Error.HttpError -> {
                logger.error("observeSeasonDetails", "$response")
                throw Throwable("${response.code} - ${response.errorBody?.message}")
            }

            is ApiResponse.Error.SerializationError -> {
                logger.error("observeSeasonDetails", "$response")
                throw Throwable("$response")
            }

            is ApiResponse.Error.JsonConvertException -> {
                logger.error("observeSeasonDetails", "$response")
                throw Throwable("$response")
            }
        }
    }
}
