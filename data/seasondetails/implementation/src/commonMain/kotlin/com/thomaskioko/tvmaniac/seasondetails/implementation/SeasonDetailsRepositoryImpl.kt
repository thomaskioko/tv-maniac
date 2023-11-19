package com.thomaskioko.tvmaniac.seasondetails.implementation

import com.thomaskioko.tvmaniac.core.db.Season
import com.thomaskioko.tvmaniac.core.db.SeasonEpisodeDetailsById
import com.thomaskioko.tvmaniac.core.networkutil.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.core.networkutil.Failure
import com.thomaskioko.tvmaniac.core.networkutil.NetworkExceptionHandler
import com.thomaskioko.tvmaniac.core.networkutil.networkBoundResult
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.episodes.api.EpisodesDao
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasons.api.SeasonsDao
import com.thomaskioko.tvmaniac.trakt.api.TraktShowsRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.ErrorResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSeasonEpisodesResponse
import com.thomaskioko.tvmaniac.util.KermitLogger
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class SeasonDetailsRepositoryImpl(
    private val remoteDataSource: TraktShowsRemoteDataSource,
    private val seasonCache: SeasonsDao,
    private val episodesDao: EpisodesDao,
    private val exceptionHandler: NetworkExceptionHandler,
    private val dispatcher: AppCoroutineDispatchers,
    private val logger: KermitLogger,
) : SeasonDetailsRepository {

    override fun observeSeasonDetailsStream(traktId: Long): Flow<Either<Failure, List<SeasonEpisodeDetailsById>>> =
        networkBoundResult(
            query = { seasonCache.observeSeasonEpisodeDetailsById(traktId) },
            shouldFetch = { it.isNullOrEmpty() },
            fetch = { remoteDataSource.getSeasonEpisodes(traktId) },
            saveFetchResult = { mapResponse(traktId, it) },
            exceptionHandler = exceptionHandler,
            coroutineDispatcher = dispatcher.io,
        )

    override suspend fun fetchSeasonDetails(traktId: Long): List<SeasonEpisodeDetailsById> =
        seasonCache.fetchSeasonDetails(traktId)

    private fun mapResponse(
        showId: Long,
        response: ApiResponse<List<TraktSeasonEpisodesResponse>, ErrorResponse>,
    ) {
        when (response) {
            is ApiResponse.Success -> {
                response.body.forEach { season ->
                    episodesDao.insert(season.toEpisodeCacheList())

                    seasonCache.upsert(
                        Season(
                            id = Id(season.ids.trakt.toLong()),
                            show_id = Id(showId),
                            season_number = season.number.toLong(),
                            title = season.title,
                            episode_count = season.episodeCount.toLong(),
                            overview = season.overview,
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
                throw Throwable("${response.code} - ${response.errorMessage}")
            }

            is ApiResponse.Error.SerializationError -> {
                logger.error("observeSeasonDetails", "${response.errorMessage}")
                throw Throwable("${response.errorMessage}")
            }
        }
    }
}
