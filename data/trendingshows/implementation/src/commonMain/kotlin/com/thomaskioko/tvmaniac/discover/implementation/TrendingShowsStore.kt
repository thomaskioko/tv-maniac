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
import com.thomaskioko.tvmaniac.util.FormatterUtil
import com.thomaskioko.tvmaniac.util.PlatformDateFormatter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
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
    fetcher = apiFetcher { params: TrendingShowsParams ->
        tmdbRemoteDataSource.getTrendingShows(params.timeWindow).also {
            requestManagerRepository.upsert(
                entityId = TRENDING_SHOWS_TODAY.requestId,
                requestType = TRENDING_SHOWS_TODAY.name,
            )
        }
    },
    sourceOfTruth = SourceOfTruth.of<TrendingShowsParams, TmdbShowResult, List<ShowEntity>>(
        reader = { param: TrendingShowsParams ->
            trendingShowsDao.observeTrendingShows(param.page).map { shows ->
                when {
                    shows.isEmpty() -> null
                    !requestManagerRepository.isRequestValid(
                        requestType = TRENDING_SHOWS_TODAY.name,
                        threshold = TRENDING_SHOWS_TODAY.duration,
                    ) -> null
                    else -> shows
                }
            }
        },
        writer = { _: TrendingShowsParams, trendingShows ->
            databaseTransactionRunner {
                val entries = trendingShows.results.map { show ->
                    val showId = show.id.toLong()

                    if (!tvShowsDao.showExists(showId)) {
                        val placeholder = createShowPlaceholder(
                            id = showId,
                            name = show.name,
                            overview = show.overview,
                            posterPath = show.posterPath?.let { formatterUtil.formatTmdbPosterPath(it) },
                            popularity = show.popularity,
                            voteAverage = show.voteAverage,
                            voteCount = show.voteCount.toLong(),
                            genreIds = show.genreIds,
                        )
                        tvShowsDao.upsert(placeholder)
                    }

                    Trending_shows(
                        id = Id(showId),
                        page = Id(trendingShows.page.toLong()),
                        name = show.name,
                        poster_path = show.posterPath?.let { formatterUtil.formatTmdbPosterPath(it) },
                        overview = show.overview,
                    )
                }

                if (trendingShows.page == 1) {
                    trendingShowsDao.deleteTrendingShows()
                }

                entries.forEach { entry ->
                    trendingShowsDao.upsert(entry)
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
