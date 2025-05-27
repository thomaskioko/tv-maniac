package com.thomaskioko.tvmaniac.discover.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.model.ApiResponse
import com.thomaskioko.tvmaniac.core.store.storeBuilder
import com.thomaskioko.tvmaniac.core.store.usingDispatchers
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Trending_shows
import com.thomaskioko.tvmaniac.db.Tvshow
import com.thomaskioko.tvmaniac.discover.api.TrendingShowsDao
import com.thomaskioko.tvmaniac.discover.api.TrendingShowsParams
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.TRENDING_SHOWS_TODAY
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowsNetworkDataSource
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbShowResult
import com.thomaskioko.tvmaniac.util.FormatterUtil
import com.thomaskioko.tvmaniac.util.PlatformDateFormatter
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.Validator

@Inject
class TrendingShowsStore(
    private val tmdbRemoteDataSource: TmdbShowsNetworkDataSource,
    private val requestManagerRepository: RequestManagerRepository,
    private val trendingShowsDao: TrendingShowsDao,
    private val tvShowsDao: TvShowsDao,
    private val formatterUtil: FormatterUtil,
    private val dateFormatter: PlatformDateFormatter,
    private val databaseTransactionRunner: DatabaseTransactionRunner,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<TrendingShowsParams, List<ShowEntity>> by storeBuilder(
    fetcher = Fetcher.of { params: TrendingShowsParams ->
        when (val response = tmdbRemoteDataSource.getTrendingShows(params.timeWindow)) {
            is ApiResponse.Success -> response.body
            is ApiResponse.Error.GenericError -> {
                throw Throwable("${response.errorMessage}")
            }
            is ApiResponse.Error.HttpError -> {
                throw Throwable("${response.code} - ${response.errorMessage}")
            }
            is ApiResponse.Error.SerializationError -> {
                throw Throwable("${response.errorMessage}")
            }
        }
    },
    sourceOfTruth = SourceOfTruth.of<TrendingShowsParams, TmdbShowResult, List<ShowEntity>>(
        reader = { param: TrendingShowsParams -> trendingShowsDao.observeTvShow(param.page) },
        writer = { _: TrendingShowsParams, trendingShows ->
            databaseTransactionRunner {
                trendingShows.results.forEach { show ->
                    tvShowsDao.upsert(
                        Tvshow(
                            id = Id(show.id.toLong()),
                            name = show.name,
                            overview = show.overview,
                            language = show.originalLanguage,
                            status = null,
                            first_air_date = show.firstAirDate?.let { dateFormatter.getYear(it) },
                            popularity = show.popularity,
                            episode_numbers = null,
                            last_air_date = null,
                            season_numbers = null,
                            vote_average = show.voteAverage,
                            vote_count = show.voteCount.toLong(),
                            genre_ids = show.genreIds,
                            poster_path = show.posterPath?.let { formatterUtil.formatTmdbPosterPath(it) },
                            backdrop_path = show.backdropPath?.let { formatterUtil.formatTmdbPosterPath(it) },
                        ),
                    )

                    trendingShowsDao.upsert(
                        Trending_shows(
                            id = Id(show.id.toLong()),
                            page = Id(trendingShows.page.toLong()),
                        ),
                    )
                }
                requestManagerRepository.upsert(
                    entityId = TRENDING_SHOWS_TODAY.requestId + trendingShows.page,
                    requestType = TRENDING_SHOWS_TODAY.name,
                )
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
                requestType = TRENDING_SHOWS_TODAY.name,
                threshold = TRENDING_SHOWS_TODAY.duration,
            )
        }
    },
).build()
