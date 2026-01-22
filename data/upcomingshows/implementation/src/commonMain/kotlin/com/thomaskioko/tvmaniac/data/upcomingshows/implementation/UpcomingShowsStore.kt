package com.thomaskioko.tvmaniac.data.upcomingshows.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.model.ApiResponse
import com.thomaskioko.tvmaniac.core.store.storeBuilder
import com.thomaskioko.tvmaniac.core.store.usingDispatchers
import com.thomaskioko.tvmaniac.data.upcomingshows.api.UpcomingShowsDao
import com.thomaskioko.tvmaniac.data.upcomingshows.implementation.model.UpcomingParams
import com.thomaskioko.tvmaniac.data.upcomingshows.implementation.model.UpcomingShowResult
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Tvshow
import com.thomaskioko.tvmaniac.db.Upcoming_shows
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.UPCOMING_SHOWS
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowsNetworkDataSource
import com.thomaskioko.tvmaniac.trakt.api.TraktShowsRemoteDataSource
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import com.thomaskioko.tvmaniac.util.api.FormatterUtil
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.Validator

@Inject
public class UpcomingShowsStore(
    private val tmdbRemoteDataSource: TmdbShowsNetworkDataSource,
    private val traktRemoteDataSource: TraktShowsRemoteDataSource,
    private val requestManagerRepository: RequestManagerRepository,
    private val upcomingShowsDao: UpcomingShowsDao,
    private val tvShowsDao: TvShowsDao,
    private val formatterUtil: FormatterUtil,
    private val dateTimeProvider: DateTimeProvider,
    private val databaseTransactionRunner: DatabaseTransactionRunner,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<UpcomingParams, List<ShowEntity>> by storeBuilder(
    fetcher = Fetcher.of { params: UpcomingParams ->
        coroutineScope {
            val tmdbResult = tmdbRemoteDataSource.getUpComingShows(
                page = params.page,
                firstAirDate = params.startDate,
                lastAirDate = params.endDate,
            )

            when (tmdbResult) {
                is ApiResponse.Success -> {
                    tmdbResult.body.results
                        .map { tmdbShow ->
                            async {
                                val traktResult = runCatching {
                                    traktRemoteDataSource.getShowByTmdbId(tmdbShow.id.toLong())
                                }
                                val traktShow = traktResult.getOrNull()?.let {
                                    (it as? ApiResponse.Success)?.body?.firstOrNull { result ->
                                        result.type == "show"
                                    }?.show
                                }
                                UpcomingShowResult(
                                    tmdbShow = tmdbShow,
                                    traktShow = traktShow,
                                )
                            }
                        }
                        .awaitAll()
                }
                is ApiResponse.Error -> throw tmdbResult.toException()
            }
        }
    },
    sourceOfTruth = SourceOfTruth.of<UpcomingParams, List<UpcomingShowResult>, List<ShowEntity>>(
        reader = { param -> upcomingShowsDao.observeUpcomingShows(param.page) },
        writer = { params: UpcomingParams, response ->
            withContext(dispatchers.databaseWrite) {
                databaseTransactionRunner {
                    if (params.page == 1L) {
                        upcomingShowsDao.deleteUpcomingShows()
                        requestManagerRepository.upsert(
                            entityId = UPCOMING_SHOWS.requestId,
                            requestType = UPCOMING_SHOWS.name,
                        )
                    }

                    response.forEach { result ->
                        val traktId = result.traktShow!!.ids.trakt
                        val tmdbId = result.tmdbShow.id.toLong()

                        tvShowsDao.upsertMerging(result.toTvshow(traktId, tmdbId, formatterUtil, dateTimeProvider))

                        upcomingShowsDao.upsert(
                            Upcoming_shows(
                                trakt_id = Id(traktId),
                                tmdb_id = Id(tmdbId),
                                page = Id(params.page),
                                name = result.tmdbShow.name,
                                poster_path = result.tmdbShow.posterPath?.let { formatterUtil.formatTmdbPosterPath(it) },
                                overview = result.tmdbShow.overview,
                            ),
                        )
                    }
                }
            }
        },
    ).usingDispatchers(
        readDispatcher = dispatchers.databaseRead,
        writeDispatcher = dispatchers.databaseWrite,
    ),
).validator(
    Validator.by {
        withContext(dispatchers.io) {
            requestManagerRepository.isRequestValid(
                requestType = UPCOMING_SHOWS.name,
                threshold = UPCOMING_SHOWS.duration,
            )
        }
    },
).build()

private fun UpcomingShowResult.toTvshow(
    traktId: Long,
    tmdbId: Long,
    formatterUtil: FormatterUtil,
    dateTimeProvider: DateTimeProvider,
): Tvshow {
    val tmdb = tmdbShow
    val trakt = traktShow
    val dateString = tmdb.firstAirDate ?: trakt?.firstAirDate
    return Tvshow(
        trakt_id = Id(traktId),
        tmdb_id = Id(tmdbId),
        name = tmdb.name,
        overview = tmdb.overview,
        language = tmdb.originalLanguage ?: trakt?.language,
        year = dateString?.let { dateTimeProvider.extractYear(it) },
        ratings = tmdb.voteAverage,
        vote_count = tmdb.voteCount.toLong(),
        poster_path = tmdb.posterPath?.let { formatterUtil.formatTmdbPosterPath(it) },
        backdrop_path = tmdb.backdropPath?.let { formatterUtil.formatTmdbPosterPath(it) },
        status = trakt?.status,
        genres = trakt?.genres?.map { it.replaceFirstChar { char -> char.uppercase() } },
        episode_numbers = trakt?.airedEpisodes?.toString(),
        season_numbers = null,
    )
}

private fun ApiResponse.Error<*>.toException(): Exception = when (this) {
    is ApiResponse.Error.HttpError -> Exception("HTTP error: $code - $errorMessage")
    is ApiResponse.Error.SerializationError -> Exception("Serialization error: $message")
    is ApiResponse.Error.GenericError -> Exception("Error: $message")
}
