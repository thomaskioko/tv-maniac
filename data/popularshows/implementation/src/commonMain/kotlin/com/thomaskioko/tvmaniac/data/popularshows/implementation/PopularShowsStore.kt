package com.thomaskioko.tvmaniac.data.popularshows.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.store.apiFetcher
import com.thomaskioko.tvmaniac.core.store.storeBuilder
import com.thomaskioko.tvmaniac.core.store.usingDispatchers
import com.thomaskioko.tvmaniac.data.popularshows.api.PopularShowsDao
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Popular_shows
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.POPULAR_SHOWS
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.shows.api.createShowPlaceholder
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowsNetworkDataSource
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbShowResult
import com.thomaskioko.tvmaniac.util.FormatterUtil
import com.thomaskioko.tvmaniac.util.PlatformDateFormatter
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.Validator

@Inject
class PopularShowsStore(
    private val tmdbRemoteDataSource: TmdbShowsNetworkDataSource,
    private val requestManagerRepository: RequestManagerRepository,
    private val dateFormatter: PlatformDateFormatter,
    private val popularShowsDao: PopularShowsDao,
    private val tvShowsDao: TvShowsDao,
    private val formatterUtil: FormatterUtil,
    private val databaseTransactionRunner: DatabaseTransactionRunner,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<Long, List<ShowEntity>> by storeBuilder(
    fetcher = apiFetcher { page ->
        tmdbRemoteDataSource.getPopularShows(page = page).also {
            requestManagerRepository.upsert(
                entityId = POPULAR_SHOWS.requestId,
                requestType = POPULAR_SHOWS.name,
            )
        }
    },
    sourceOfTruth = SourceOfTruth.of<Long, TmdbShowResult, List<ShowEntity>>(
        reader = { page ->
            popularShowsDao.observePopularShows(page).map { shows ->
                when {
                    shows.isEmpty() -> null
                    !requestManagerRepository.isRequestValid(
                        requestType = POPULAR_SHOWS.name,
                        threshold = POPULAR_SHOWS.duration,
                    ) -> null
                    else -> shows
                }
            }
        },
        writer = { _, popularShows ->
            databaseTransactionRunner {
                val entries = popularShows.results.map { show ->
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

                    Popular_shows(
                        id = Id(showId),
                        page = Id(popularShows.page.toLong()),
                        name = show.name,
                        poster_path = show.posterPath?.let { formatterUtil.formatTmdbPosterPath(it) },
                        overview = show.overview,
                    )
                }

                if (popularShows.page == 1) {
                    popularShowsDao.deletePopularShows()
                }

                entries.forEach { entry ->
                    popularShowsDao.upsert(entry)
                }
            }
        },
        delete = popularShowsDao::deletePopularShow,
        deleteAll = popularShowsDao::deletePopularShows,
    ).usingDispatchers(
        readDispatcher = dispatchers.databaseRead,
        writeDispatcher = dispatchers.databaseWrite,
    ),
).validator(
    Validator.by {
        withContext(dispatchers.io) {
            requestManagerRepository.isRequestValid(
                requestType = POPULAR_SHOWS.name,
                threshold = POPULAR_SHOWS.duration,
            )
        }
    },
).build()
