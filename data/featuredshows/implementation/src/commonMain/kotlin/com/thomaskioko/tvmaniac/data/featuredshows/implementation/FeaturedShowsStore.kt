package com.thomaskioko.tvmaniac.data.featuredshows.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.model.getOrThrow
import com.thomaskioko.tvmaniac.core.store.storeBuilder
import com.thomaskioko.tvmaniac.core.store.usingDispatchers
import com.thomaskioko.tvmaniac.data.featuredshows.api.FeaturedShowsDao
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.Featured_shows
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Tvshow
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.FEATURED_SHOWS_TODAY
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowDetailsNetworkDataSource
import com.thomaskioko.tvmaniac.trakt.api.TraktShowsRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowResponse
import com.thomaskioko.tvmaniac.util.FormatterUtil
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.Validator

@Inject
public class FeaturedShowsStore(
    private val traktRemoteDataSource: TraktShowsRemoteDataSource,
    private val tmdbDetailsDataSource: TmdbShowDetailsNetworkDataSource,
    private val requestManagerRepository: RequestManagerRepository,
    private val featuredShowsDao: FeaturedShowsDao,
    private val tvShowsDao: TvShowsDao,
    private val formatterUtil: FormatterUtil,
    private val databaseTransactionRunner: DatabaseTransactionRunner,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<Long, List<ShowEntity>> by storeBuilder(
    fetcher = Fetcher.of { page: Long ->
        coroutineScope {
            traktRemoteDataSource.getTrendingShows(page = page.toInt()).getOrThrow()
                .filter { it.show.ids.tmdb != null }
                .mapIndexed { index, traktResponse ->
                    async {
                        val tmdbId = traktResponse.show.ids.tmdb!!.toLong()
                        when (val tmdbDetails = tmdbDetailsDataSource.getShowDetails(tmdbId)) {
                            is ApiResponse.Success -> FeaturedShowWithImages(
                                traktShow = traktResponse.show,
                                tmdbPosterPath = tmdbDetails.body.posterPath,
                                tmdbBackdropPath = tmdbDetails.body.backdropPath,
                                pageOrder = index,
                            )

                            is ApiResponse.Error -> FeaturedShowWithImages(
                                traktShow = traktResponse.show,
                                tmdbPosterPath = null,
                                tmdbBackdropPath = null,
                                pageOrder = index,
                            )
                        }
                    }
                }
                .awaitAll()
        }
    },
    sourceOfTruth = SourceOfTruth.of<Long, List<FeaturedShowWithImages>, List<ShowEntity>>(
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
        writer = { page, response ->
            withContext(dispatchers.databaseWrite) {
                databaseTransactionRunner {
                    if (page == 1L) {
                        featuredShowsDao.deleteFeaturedShows()
                    }

                    response.forEach { showWithImages ->
                        val show = showWithImages.traktShow
                        val showId = show.ids.tmdb!!.toLong()
                        val posterPath = showWithImages.tmdbPosterPath?.let {
                            formatterUtil.formatTmdbPosterPath(it)
                        }
                        val backdropPath = showWithImages.tmdbBackdropPath?.let {
                            formatterUtil.formatTmdbPosterPath(it)
                        }

                        if (!tvShowsDao.showExists(showId)) {
                            tvShowsDao.upsert(show.toTvshow(showId, posterPath, backdropPath))
                        }

                        featuredShowsDao.upsert(
                            Featured_shows(
                                id = Id(showId),
                                name = show.title,
                                poster_path = posterPath,
                                overview = show.overview,
                                page_order = showWithImages.pageOrder.toLong(),
                            ),
                        )
                    }
                }

                requestManagerRepository.upsert(
                    entityId = FEATURED_SHOWS_TODAY.requestId,
                    requestType = FEATURED_SHOWS_TODAY.name,
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
                requestType = FEATURED_SHOWS_TODAY.name,
                threshold = FEATURED_SHOWS_TODAY.duration,
            )
        }
    },
).build()

private data class FeaturedShowWithImages(
    val traktShow: TraktShowResponse,
    val tmdbPosterPath: String?,
    val tmdbBackdropPath: String?,
    val pageOrder: Int,
)

private fun TraktShowResponse.toTvshow(
    showId: Long,
    posterPath: String?,
    backdropPath: String?,
): Tvshow = Tvshow(
    id = Id(showId),
    name = title,
    overview = overview ?: "",
    language = language,
    first_air_date = firstAirDate,
    popularity = 0.0,
    vote_average = rating ?: 0.0,
    vote_count = votes?.toLong() ?: 0L,
    poster_path = posterPath,
    backdrop_path = backdropPath,
    status = status,
    genre_ids = emptyList(),
    episode_numbers = null,
    last_air_date = null,
    season_numbers = null,
)
