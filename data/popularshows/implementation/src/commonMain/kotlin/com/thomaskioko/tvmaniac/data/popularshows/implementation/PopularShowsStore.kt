package com.thomaskioko.tvmaniac.data.popularshows.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.store.apiFetcher
import com.thomaskioko.tvmaniac.core.store.storeBuilder
import com.thomaskioko.tvmaniac.core.store.usingDispatchers
import com.thomaskioko.tvmaniac.data.popularshows.api.PopularShowsDao
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Popular_shows
import com.thomaskioko.tvmaniac.db.Tvshow
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.POPULAR_SHOWS
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowsNetworkDataSource
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbShowResult
import com.thomaskioko.tvmaniac.util.FormatterUtil
import com.thomaskioko.tvmaniac.util.PlatformDateFormatter
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
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
    fetcher = apiFetcher { page -> tmdbRemoteDataSource.getPopularShows(page = page) },
    sourceOfTruth = SourceOfTruth.of<Long, TmdbShowResult, List<ShowEntity>>(
        reader = { page -> popularShowsDao.observePopularShows(page) },
        writer = { _, trendingShows ->
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

                    popularShowsDao.upsert(
                        Popular_shows(
                            id = Id(show.id.toLong()),
                            page = Id(trendingShows.page.toLong()),
                        ),
                    )
                }

                requestManagerRepository.upsert(
                    entityId = POPULAR_SHOWS.requestId,
                    requestType = POPULAR_SHOWS.name,
                )
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
