package com.thomaskioko.tvmaniac.data.featuredshows.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.store.apiFetcher
import com.thomaskioko.tvmaniac.core.store.storeBuilder
import com.thomaskioko.tvmaniac.core.store.usingDispatchers
import com.thomaskioko.tvmaniac.data.featuredshows.api.FeaturedShowsDao
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.Featured_shows
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.FEATURED_SHOWS_TODAY
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.shows.api.createShowPlaceholder
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowsNetworkDataSource
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbShowResult
import com.thomaskioko.tvmaniac.util.FormatterUtil
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.Validator

@Inject
class FeaturedShowsStore(
    private val tmdbRemoteDataSource: TmdbShowsNetworkDataSource,
    private val requestManagerRepository: RequestManagerRepository,
    private val featuredShowsDao: FeaturedShowsDao,
    private val tvShowsDao: TvShowsDao,
    private val formatterUtil: FormatterUtil,
    private val databaseTransactionRunner: DatabaseTransactionRunner,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<Long, List<ShowEntity>> by storeBuilder(
    fetcher = apiFetcher { page ->
        tmdbRemoteDataSource.discoverShows(page = page).also {
            requestManagerRepository.upsert(
                entityId = FEATURED_SHOWS_TODAY.requestId,
                requestType = FEATURED_SHOWS_TODAY.name,
            )
        }
    },
    sourceOfTruth = SourceOfTruth.of<Long, TmdbShowResult, List<ShowEntity>>(
        reader = { page: Long ->
            featuredShowsDao.observeFeaturedShows(page).map { shows ->
                when {
                    shows.isEmpty() -> null
                    !requestManagerRepository.isRequestValid(
                        requestType = FEATURED_SHOWS_TODAY.name,
                        threshold = FEATURED_SHOWS_TODAY.duration,
                    ) -> null
                    else -> shows
                }
            }
        },
        writer = { _, response ->
            databaseTransactionRunner {
                featuredShowsDao.deleteFeaturedShows()

                val entries = response.results.map { show ->
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

                    Featured_shows(
                        id = Id(showId),
                        name = show.name,
                        poster_path = show.posterPath?.let { formatterUtil.formatTmdbPosterPath(it) },
                        overview = show.overview,
                    )
                }

                entries.forEach { entry ->
                    featuredShowsDao.upsert(entry)
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
                requestType = FEATURED_SHOWS_TODAY.name,
                threshold = FEATURED_SHOWS_TODAY.duration,
            )
        }
    },
).build()
