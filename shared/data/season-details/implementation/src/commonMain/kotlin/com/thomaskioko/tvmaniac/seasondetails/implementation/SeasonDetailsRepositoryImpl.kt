package com.thomaskioko.tvmaniac.seasondetails.implementation

import co.touchlab.kermit.Logger
import com.thomaskioko.tvmaniac.core.db.Season_episodes
import com.thomaskioko.tvmaniac.core.db.Seasons
import com.thomaskioko.tvmaniac.core.db.SelectSeasonWithEpisodes
import com.thomaskioko.tvmaniac.core.networkutil.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.DefaultError
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.core.networkutil.Failure
import com.thomaskioko.tvmaniac.core.networkutil.networkBoundResult
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.episodes.api.EpisodesCache
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonsCache
import com.thomaskioko.tvmaniac.trakt.api.TraktService
import com.thomaskioko.tvmaniac.trakt.api.model.ErrorResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSeasonEpisodesResponse
import com.thomaskioko.tvmaniac.util.ExceptionHandler
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import me.tatarka.inject.annotations.Inject

@Inject
class SeasonDetailsRepositoryImpl(
    private val traktService: TraktService,
    private val seasonCache: SeasonsCache,
    private val episodesCache: EpisodesCache,
    private val datastore: DatastoreRepository,
    private val exceptionHandler: ExceptionHandler,
    private val dispatcher: AppCoroutineDispatchers,
) : SeasonDetailsRepository {

    override fun observeSeasonsStream(traktId: Long): Flow<Either<Failure, List<Seasons>>> =
        networkBoundResult(
            query = { seasonCache.observeSeasons(traktId) },
            shouldFetch = { it.isNullOrEmpty() },
            fetch = { traktService.getShowSeasons(traktId) },
            saveFetchResult = {
                when (it) {
                    is ApiResponse.Success -> seasonCache.insertSeasons(
                        it.body.toSeasonCacheList(
                            traktId,
                        ),
                    )

                    is ApiResponse.Error.GenericError -> {
                        Logger.withTag("observeSeasons").e("$it")
                        throw Throwable("${it.errorMessage}")
                    }

                    is ApiResponse.Error.HttpError -> {
                        Logger.withTag("observeSeasons").e("$it")
                        throw Throwable("${it.code} - ${it.errorBody?.message}")
                    }

                    is ApiResponse.Error.SerializationError -> {
                        Logger.withTag("observeSeasons").e("$it")
                        throw Throwable("$it")
                    }
                }
            },
            exceptionHandler = exceptionHandler,
            coroutineDispatcher = dispatcher.io,
        )

    override fun observeSeasonDetailsStream(traktId: Long): Flow<Either<Failure, List<SelectSeasonWithEpisodes>>> =
        networkBoundResult(
            query = {
                datastore.saveSeasonId(traktId)
                seasonCache.observeShowEpisodes(traktId)
            },
            shouldFetch = { it.isNullOrEmpty() },
            fetch = { traktService.getSeasonEpisodes(traktId) },
            saveFetchResult = { mapResponse(traktId, it) },
            exceptionHandler = exceptionHandler,
            coroutineDispatcher = dispatcher.io,
        )

    override fun observeSeasonDetails(): Flow<Either<Failure, List<SelectSeasonWithEpisodes>>> =
        flow {
            datastore.getSeasonId()
                .collect {
                    seasonCache.observeShowEpisodes(it)
                        .catch { emit(Either.Left(DefaultError(exceptionHandler.resolveError(it)))) }
                        .collect { emit(Either.Right(it)) }
                }
        }

    private fun mapResponse(
        showId: Long,
        response: ApiResponse<List<TraktSeasonEpisodesResponse>, ErrorResponse>,
    ) {
        when (response) {
            is ApiResponse.Success -> {
                response.body.forEach { season ->
                    episodesCache.insert(season.toEpisodeCacheList())

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
                Logger.withTag("observeSeasonDetails").e("$response")
                throw Throwable("${response.errorMessage}")
            }

            is ApiResponse.Error.HttpError -> {
                Logger.withTag("observeSeasonDetails").e("$response")
                throw Throwable("${response.code} - ${response.errorBody?.message}")
            }

            is ApiResponse.Error.SerializationError -> {
                Logger.withTag("observeSeasonDetails").e("$response")
                throw Throwable("$response")
            }
        }
    }
}
