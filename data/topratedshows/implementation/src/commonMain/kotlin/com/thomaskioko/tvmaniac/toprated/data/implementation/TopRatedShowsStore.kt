package com.thomaskioko.tvmaniac.toprated.data.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.store.apiFetcher
import com.thomaskioko.tvmaniac.core.store.storeBuilder
import com.thomaskioko.tvmaniac.core.store.usingDispatchers
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Toprated_shows
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.TOP_RATED_SHOWS
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.shows.api.createShowPlaceholder
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowsNetworkDataSource
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbShowResult
import com.thomaskioko.tvmaniac.topratedshows.data.api.TopRatedShowsDao
import com.thomaskioko.tvmaniac.util.FormatterUtil
import com.thomaskioko.tvmaniac.util.PlatformDateFormatter
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.Validator

@Inject
class TopRatedShowsStore(
    private val tmdbRemoteDataSource: TmdbShowsNetworkDataSource,
    private val requestManagerRepository: RequestManagerRepository,
    private val topRatedShowsDao: TopRatedShowsDao,
    private val tvShowsDao: TvShowsDao,
    private val formatterUtil: FormatterUtil,
    private val dateFormatter: PlatformDateFormatter,
    private val databaseTransactionRunner: DatabaseTransactionRunner,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<Long, List<ShowEntity>> by storeBuilder(
    fetcher = apiFetcher { page ->
        tmdbRemoteDataSource.getTopRatedShows(page = page).also {
            // Update timestamp BEFORE writing to database to ensure reader validation sees fresh timestamp
            requestManagerRepository.upsert(
                entityId = TOP_RATED_SHOWS.requestId,
                requestType = TOP_RATED_SHOWS.name,
            )
        }
    },
    sourceOfTruth = SourceOfTruth.of<Long, TmdbShowResult, List<ShowEntity>>(
        reader = { page ->
            topRatedShowsDao.observeTopRatedShows(page).map { shows ->
                when {
                    shows.isEmpty() -> null // No data, force fetch
                    !requestManagerRepository.isRequestValid(
                        requestType = TOP_RATED_SHOWS.name,
                        threshold = TOP_RATED_SHOWS.duration,
                    ) -> null // Stale data, force fetch
                    else -> shows // Return show data directly from toprated_shows table - completely stable!
                }
            }
        },
        writer = { _, topRatedShows ->
            databaseTransactionRunner {
                // Store show data directly in toprated_shows table for complete stability
                val entries = topRatedShows.results.map { show ->
                    val showId = show.id.toLong()

                    // Also create placeholder in tvshow table if needed (for other parts of app)
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

                    // Store show data directly in toprated_shows table
                    Toprated_shows(
                        id = Id(showId),
                        page = Id(topRatedShows.page.toLong()),
                        name = show.name,
                        poster_path = show.posterPath?.let { formatterUtil.formatTmdbPosterPath(it) },
                        overview = show.overview,
                    )
                }

                // Clear existing entries for page 1 to maintain consistency
                if (topRatedShows.page == 1) {
                    topRatedShowsDao.deleteTrendingShows()
                }

                // Insert the new entries
                entries.forEach { entry ->
                    topRatedShowsDao.upsert(entry)
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
                requestType = TOP_RATED_SHOWS.name,
                threshold = TOP_RATED_SHOWS.duration,
            )
        }
    },
).build()
