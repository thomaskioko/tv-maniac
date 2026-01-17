package com.thomaskioko.tvmaniac.toprated.data.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.model.getOrThrow
import com.thomaskioko.tvmaniac.core.store.storeBuilder
import com.thomaskioko.tvmaniac.core.store.usingDispatchers
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Toprated_shows
import com.thomaskioko.tvmaniac.db.Tvshow
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.TOP_RATED_SHOWS
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowDetailsNetworkDataSource
import com.thomaskioko.tvmaniac.topratedshows.data.api.TopRatedShowsDao
import com.thomaskioko.tvmaniac.trakt.api.TraktShowsRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowResponse
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
public class TopRatedShowsStore(
    private val traktRemoteDataSource: TraktShowsRemoteDataSource,
    private val tmdbDetailsDataSource: TmdbShowDetailsNetworkDataSource,
    private val requestManagerRepository: RequestManagerRepository,
    private val topRatedShowsDao: TopRatedShowsDao,
    private val tvShowsDao: TvShowsDao,
    private val formatterUtil: FormatterUtil,
    private val databaseTransactionRunner: DatabaseTransactionRunner,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<Long, List<ShowEntity>> by storeBuilder(
    fetcher = Fetcher.of { page: Long ->
        coroutineScope {
            traktRemoteDataSource.getFavoritedShows(page = page.toInt(), limit = 20).getOrThrow()
                .withIndex()
                .mapNotNull { (index, traktResponse) ->
                    val tmdbId = traktResponse.show.ids.tmdb ?: return@mapNotNull null
                    async {
                        when (val tmdbDetails = tmdbDetailsDataSource.getShowDetails(tmdbId)) {
                            is ApiResponse.Success -> TopRatedShowWithImages(
                                traktShow = traktResponse.show,
                                tmdbId = tmdbId,
                                tmdbPosterPath = tmdbDetails.body.posterPath,
                                tmdbBackdropPath = tmdbDetails.body.backdropPath,
                                pageOrder = index,
                            )

                            is ApiResponse.Error -> TopRatedShowWithImages(
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
    sourceOfTruth = SourceOfTruth.of<Long, List<TopRatedShowWithImages>, List<ShowEntity>>(
        reader = { page -> topRatedShowsDao.observeTopRatedShows(page) },
        writer = { page, response ->
            withContext(dispatchers.databaseWrite) {
                databaseTransactionRunner {
                    if (page == 1L) {
                        topRatedShowsDao.deleteTrendingShows()
                        requestManagerRepository.upsert(
                            entityId = TOP_RATED_SHOWS.requestId,
                            requestType = TOP_RATED_SHOWS.name,
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

                        tvShowsDao.upsertMerging(show.toTvshow(traktId, tmdbId, posterPath, backdropPath))

                        topRatedShowsDao.upsert(
                            Toprated_shows(
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
        delete = topRatedShowsDao::deleteTrendingShows,
        deleteAll = topRatedShowsDao::deleteTrendingShows,
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

private fun TraktShowResponse.toTvshow(
    traktId: Long,
    tmdbId: Long,
    posterPath: String?,
    backdropPath: String?,
): Tvshow = Tvshow(
    trakt_id = Id(traktId),
    tmdb_id = Id(tmdbId),
    name = title,
    overview = overview ?: "",
    language = language,
    year = firstAirDate,
    ratings = rating ?: 0.0,
    vote_count = votes?.toLong() ?: 0L,
    poster_path = posterPath,
    backdrop_path = backdropPath,
    status = status,
    genres = genres?.map { it.replaceFirstChar { char -> char.uppercase() } },
    episode_numbers = airedEpisodes?.toString(),
    season_numbers = null,
)
