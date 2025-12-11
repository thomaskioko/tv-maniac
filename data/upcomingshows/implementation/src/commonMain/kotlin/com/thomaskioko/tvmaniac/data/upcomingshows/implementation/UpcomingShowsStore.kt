package com.thomaskioko.tvmaniac.data.upcomingshows.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.store.apiFetcher
import com.thomaskioko.tvmaniac.core.store.storeBuilder
import com.thomaskioko.tvmaniac.core.store.usingDispatchers
import com.thomaskioko.tvmaniac.data.upcomingshows.api.UpcomingShowsDao
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Upcoming_shows
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.UPCOMING_SHOWS
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.shows.api.createShowPlaceholder
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowsNetworkDataSource
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbShowResult
import com.thomaskioko.tvmaniac.util.api.FormatterUtil
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.Validator

@Inject
class UpcomingShowsStore(
    private val tmdbRemoteDataSource: TmdbShowsNetworkDataSource,
    private val requestManagerRepository: RequestManagerRepository,
    private val upcomingShowsDao: UpcomingShowsDao,
    private val tvShowsDao: TvShowsDao,
    private val formatterUtil: FormatterUtil,
    private val databaseTransactionRunner: DatabaseTransactionRunner,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<UpcomingParams, List<ShowEntity>> by storeBuilder(
    fetcher = apiFetcher { params: UpcomingParams ->
        tmdbRemoteDataSource.getUpComingShows(
            page = params.page,
            firstAirDate = params.startDate,
            lastAirDate = params.endDate,
        ).also {
            // Update timestamp BEFORE writing to database to ensure reader validation sees fresh timestamp
            requestManagerRepository.upsert(
                entityId = UPCOMING_SHOWS.requestId,
                requestType = UPCOMING_SHOWS.name,
            )
        }
    },
    sourceOfTruth = SourceOfTruth.of(
        reader = { param ->
            upcomingShowsDao.observeUpcomingShows(param.page).map { shows ->
                when {
                    shows.isEmpty() -> null // No data, force fetch
                    !requestManagerRepository.isRequestValid(
                        requestType = UPCOMING_SHOWS.name,
                        threshold = UPCOMING_SHOWS.duration,
                    ) -> null // Stale data, force fetch
                    else -> shows // Return show data directly from upcoming_shows table - completely stable!
                }
            }
        },
        writer = { _: UpcomingParams, upcomingShows: TmdbShowResult ->
            databaseTransactionRunner {
                // Store show data directly in upcoming_shows table for complete stability
                val entries = upcomingShows.results.map { show ->
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

                    // Store show data directly in upcoming_shows table
                    Upcoming_shows(
                        id = Id(showId),
                        page = Id(upcomingShows.page.toLong()),
                        name = show.name,
                        poster_path = show.posterPath?.let { formatterUtil.formatTmdbPosterPath(it) },
                        overview = show.overview,
                    )
                }

                // Clear existing entries for page 1 to maintain consistency
                if (upcomingShows.page == 1) {
                    upcomingShowsDao.deleteUpcomingShows()
                }

                // Insert the new entries
                entries.forEach { entry ->
                    upcomingShowsDao.upsert(entry)
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
