package com.thomaskioko.tvmaniac.data.featuredshows.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.storeBuilder
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.usingDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.api.model.getOrThrow
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
public class FeaturedShowsStore(
    private val traktRemoteDataSource: TraktShowsRemoteDataSource,
    private val tmdbDetailsDataSource: TmdbShowDetailsNetworkDataSource,
    private val requestManagerRepository: RequestManagerRepository,
    private val featuredShowsDao: FeaturedShowsDao,
    private val tvShowsDao: TvShowsDao,
    private val formatterUtil: FormatterUtil,
    private val dateTimeProvider: DateTimeProvider,
    private val databaseTransactionRunner: DatabaseTransactionRunner,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<Long, List<ShowEntity>> by storeBuilder(
    fetcher = Fetcher.of { page: Long ->
        coroutineScope {
            traktRemoteDataSource.getTrendingShows(page = page.toInt()).getOrThrow()
                .withIndex()
                .mapNotNull { (index, traktResponse) ->
                    val tmdbId = traktResponse.show.ids.tmdb ?: return@mapNotNull null
                    async {
                        when (val tmdbDetails = tmdbDetailsDataSource.getShowDetails(tmdbId)) {
                            is ApiResponse.Success -> FeaturedShowWithImages(
                                traktShow = traktResponse.show,
                                tmdbId = tmdbId,
                                tmdbPosterPath = tmdbDetails.body.posterPath,
                                tmdbBackdropPath = tmdbDetails.body.backdropPath,
                                pageOrder = index,
                            )

                            is ApiResponse.Error -> FeaturedShowWithImages(
                                traktShow = traktResponse.show,
                                tmdbId = tmdbId,
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
        reader = { page -> featuredShowsDao.observeFeaturedShows(page) },
        writer = { page, response ->
            withContext(dispatchers.databaseWrite) {
                databaseTransactionRunner {
                    if (page == 1L) {
                        featuredShowsDao.deleteFeaturedShows()
                        requestManagerRepository.upsert(
                            entityId = FEATURED_SHOWS_TODAY.requestId,
                            requestType = FEATURED_SHOWS_TODAY.name,
                        )
                    }

                    response.forEach { showWithImages ->
                        val show = showWithImages.traktShow
                        val traktId = show.ids.trakt
                        val tmdbId = showWithImages.tmdbId
                        val posterPath = showWithImages.tmdbPosterPath?.let {
                            formatterUtil.formatTmdbPosterPath(it)
                        }
                        val backdropPath = showWithImages.tmdbBackdropPath?.let {
                            formatterUtil.formatTmdbPosterPath(it)
                        }

                        tvShowsDao.upsertMerging(show.toTvshow(traktId, tmdbId, posterPath, backdropPath, dateTimeProvider))

                        featuredShowsDao.upsert(
                            Featured_shows(
                                trakt_id = Id(traktId),
                                tmdb_id = Id(tmdbId),
                                name = show.title,
                                poster_path = posterPath,
                                overview = show.overview,
                                page_order = showWithImages.pageOrder.toLong(),
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
                requestType = FEATURED_SHOWS_TODAY.name,
                threshold = FEATURED_SHOWS_TODAY.duration,
            )
        }
    },
).build()

private data class FeaturedShowWithImages(
    val traktShow: TraktShowResponse,
    val tmdbId: Long,
    val tmdbPosterPath: String?,
    val tmdbBackdropPath: String?,
    val pageOrder: Int,
)

private fun TraktShowResponse.toTvshow(
    traktId: Long,
    tmdbId: Long,
    posterPath: String?,
    backdropPath: String?,
    dateTimeProvider: DateTimeProvider,
): Tvshow = Tvshow(
    trakt_id = Id(traktId),
    tmdb_id = Id(tmdbId),
    name = title,
    overview = overview ?: "",
    language = language,
    year = firstAirDate?.let { dateTimeProvider.extractYear(it) },
    ratings = rating ?: 0.0,
    vote_count = votes ?: 0L,
    poster_path = posterPath,
    backdrop_path = backdropPath,
    status = status,
    genres = genres?.map { it.replaceFirstChar { char -> char.uppercase() } },
    episode_numbers = airedEpisodes?.toString(),
    season_numbers = null,
)
