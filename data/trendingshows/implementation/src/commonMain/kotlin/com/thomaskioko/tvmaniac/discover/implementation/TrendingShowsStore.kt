package com.thomaskioko.tvmaniac.discover.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.store.apiFetcher
import com.thomaskioko.tvmaniac.core.store.storeBuilder
import com.thomaskioko.tvmaniac.core.store.usingDispatchers
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Trending_shows
import com.thomaskioko.tvmaniac.discover.api.TrendingShowsDao
import com.thomaskioko.tvmaniac.discover.api.TrendingShowsParams
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.TRENDING_SHOWS_TODAY
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.shows.api.createShowPlaceholder
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowsNetworkDataSource
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbShowResult
import com.thomaskioko.tvmaniac.util.api.FormatterUtil
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.Validator

@Inject
public class TrendingShowsStore(
    private val tmdbRemoteDataSource: TmdbShowsNetworkDataSource,
    private val requestManagerRepository: RequestManagerRepository,
    private val trendingShowsDao: TrendingShowsDao,
    private val tvShowsDao: TvShowsDao,
    private val formatterUtil: FormatterUtil,
    private val databaseTransactionRunner: DatabaseTransactionRunner,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<TrendingShowsParams, List<ShowEntity>> by storeBuilder(
    fetcher = apiFetcher { params: TrendingShowsParams ->
        tmdbRemoteDataSource.getTrendingShows(params.timeWindow)
    },
    sourceOfTruth = SourceOfTruth.of<TrendingShowsParams, TmdbShowResult, List<ShowEntity>>(
        reader = { param -> trendingShowsDao.observeTrendingShows(param.page) },
        writer = { _: TrendingShowsParams, trendingShows ->
            withContext(dispatchers.databaseWrite) {
                databaseTransactionRunner {
                    if (trendingShows.page == 1) {
                        trendingShowsDao.deleteTrendingShows()
                        requestManagerRepository.upsert(
                            entityId = TRENDING_SHOWS_TODAY.requestId,
                            requestType = TRENDING_SHOWS_TODAY.name,
                        )
                    }

                    val showsToInsert = mutableListOf<com.thomaskioko.tvmaniac.db.Tvshow>()
                    val trendingEntries = mutableListOf<Trending_shows>()

                    trendingShows.results
                        .forEachIndexed { index, show ->
                            val showId = show.id.toLong()
                            val formattedPosterPath = show.posterPath?.let { formatterUtil.formatTmdbPosterPath(it) }

                            if (!tvShowsDao.showExists(showId)) {
                                showsToInsert.add(
                                    createShowPlaceholder(
                                        id = showId,
                                        name = show.name,
                                        overview = show.overview,
                                        posterPath = formattedPosterPath,
                                        popularity = show.popularity,
                                        voteAverage = show.voteAverage,
                                        voteCount = show.voteCount.toLong(),
                                        genreIds = show.genreIds,
                                        firstAirDate = show.firstAirDate,
                                    ),
                                )
                            }

                            trendingEntries.add(
                                Trending_shows(
                                    id = Id(showId),
                                    page = Id(trendingShows.page.toLong()),
                                    position = index.toLong(),
                                    name = show.name,
                                    poster_path = formattedPosterPath,
                                    overview = show.overview,
                                ),
                            )
                        }

                    showsToInsert.forEach { show ->
                        tvShowsDao.upsert(show)
                    }

                    trendingEntries.forEach { show ->
                        trendingShowsDao.upsert(show)
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
                requestType = TRENDING_SHOWS_TODAY.name,
                threshold = TRENDING_SHOWS_TODAY.duration,
            )
        }
    },
).build()
