package com.thomaskioko.tvmaniac.data.popularshows.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.storeBuilder
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.usingDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.api.model.getOrThrow
import com.thomaskioko.tvmaniac.data.popularshows.api.PopularShowsDao
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Popular_shows
import com.thomaskioko.tvmaniac.db.Tvshow
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.POPULAR_SHOWS
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
public class PopularShowsStore(
    private val traktRemoteDataSource: TraktShowsRemoteDataSource,
    private val tmdbDetailsDataSource: TmdbShowDetailsNetworkDataSource,
    private val requestManagerRepository: RequestManagerRepository,
    private val popularShowsDao: PopularShowsDao,
    private val tvShowsDao: TvShowsDao,
    private val formatterUtil: FormatterUtil,
    private val dateTimeProvider: DateTimeProvider,
    private val databaseTransactionRunner: DatabaseTransactionRunner,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<Long, List<ShowEntity>> by storeBuilder(
    fetcher = Fetcher.of { page: Long ->
        coroutineScope {
            traktRemoteDataSource.getPopularShows(page = page.toInt()).getOrThrow()
                .withIndex()
                .mapNotNull { (index, show) ->
                    val tmdbId = show.ids.tmdb ?: return@mapNotNull null
                    async {
                        when (val tmdbDetails = tmdbDetailsDataSource.getShowDetails(tmdbId)) {
                            is ApiResponse.Success -> PopularShowWithImages(
                                traktShow = show,
                                tmdbId = tmdbId,
                                tmdbPosterPath = tmdbDetails.body.posterPath,
                                tmdbBackdropPath = tmdbDetails.body.backdropPath,
                                pageOrder = index,
                            )

                            is ApiResponse.Error -> PopularShowWithImages(
                                traktShow = show,
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
    sourceOfTruth = SourceOfTruth.of<Long, List<PopularShowWithImages>, List<ShowEntity>>(
        reader = { page -> popularShowsDao.observePopularShows(page) },
        writer = { page, response ->
            withContext(dispatchers.databaseWrite) {
                databaseTransactionRunner {
                    if (page == 1L) {
                        popularShowsDao.deletePopularShows()
                        requestManagerRepository.upsert(
                            entityId = POPULAR_SHOWS.requestId,
                            requestType = POPULAR_SHOWS.name,
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

                        popularShowsDao.upsert(
                            Popular_shows(
                                trakt_id = Id(traktId),
                                tmdb_id = Id(tmdbId),
                                page = Id(page),
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

private data class PopularShowWithImages(
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
