package com.thomaskioko.tvmaniac.discover.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.model.getOrThrow
import com.thomaskioko.tvmaniac.core.store.storeBuilder
import com.thomaskioko.tvmaniac.core.store.usingDispatchers
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Trending_shows
import com.thomaskioko.tvmaniac.db.Tvshow
import com.thomaskioko.tvmaniac.discover.api.TrendingShowsDao
import com.thomaskioko.tvmaniac.discover.api.TrendingShowsParams
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.TRENDING_SHOWS_TODAY
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
public class TrendingShowsStore(
    private val traktRemoteDataSource: TraktShowsRemoteDataSource,
    private val tmdbDetailsDataSource: TmdbShowDetailsNetworkDataSource,
    private val requestManagerRepository: RequestManagerRepository,
    private val trendingShowsDao: TrendingShowsDao,
    private val tvShowsDao: TvShowsDao,
    private val formatterUtil: FormatterUtil,
    private val dateTimeProvider: DateTimeProvider,
    private val databaseTransactionRunner: DatabaseTransactionRunner,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<TrendingShowsParams, List<ShowEntity>> by storeBuilder(
    fetcher = Fetcher.of { params: TrendingShowsParams ->
        coroutineScope {
            traktRemoteDataSource.getTrendingShows(page = params.page.toInt()).getOrThrow()
                .withIndex()
                .mapNotNull { (index, traktResponse) ->
                    val tmdbId = traktResponse.show.ids.tmdb ?: return@mapNotNull null
                    async {
                        when (val tmdbDetails = tmdbDetailsDataSource.getShowDetails(tmdbId)) {
                            is ApiResponse.Success -> TrendingShowWithImages(
                                traktShow = traktResponse,
                                tmdbId = tmdbId,
                                tmdbPosterPath = tmdbDetails.body.posterPath,
                                tmdbBackdropPath = tmdbDetails.body.backdropPath,
                                pageOrder = index,
                            )

                            is ApiResponse.Error -> TrendingShowWithImages(
                                traktShow = traktResponse,
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
    sourceOfTruth = SourceOfTruth.of<TrendingShowsParams, List<TrendingShowWithImages>, List<ShowEntity>>(
        reader = { param -> trendingShowsDao.observeTrendingShows(param.page) },
        writer = { params, response ->
            withContext(dispatchers.databaseWrite) {
                databaseTransactionRunner {
                    if (params.page == 1L) {
                        trendingShowsDao.deleteTrendingShows()
                        requestManagerRepository.upsert(
                            entityId = TRENDING_SHOWS_TODAY.requestId,
                            requestType = TRENDING_SHOWS_TODAY.name,
                        )
                    }

                    response.forEach { showWithImages ->
                        val show = showWithImages.traktShow.show
                        val traktId = show.ids.trakt
                        val tmdbId = showWithImages.tmdbId
                        val posterPath = showWithImages.tmdbPosterPath?.let {
                            formatterUtil.formatTmdbPosterPath(it)
                        }
                        val backdropPath = showWithImages.tmdbBackdropPath?.let {
                            formatterUtil.formatTmdbPosterPath(it)
                        }

                        tvShowsDao.upsertMerging(show.toTvShow(traktId, tmdbId, posterPath, backdropPath, dateTimeProvider))

                        trendingShowsDao.upsert(
                            Trending_shows(
                                trakt_id = Id(traktId),
                                tmdb_id = Id(tmdbId),
                                page = Id(params.page),
                                position = showWithImages.pageOrder.toLong(),
                                name = show.title,
                                poster_path = posterPath,
                                overview = show.overview,
                            ),
                        )
                    }
                }
            }
        },
        delete = { trendingShowsDao.deleteTrendingShow(it.page) },
        deleteAll = trendingShowsDao::deleteTrendingShows,
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

private fun TraktShowResponse.toTvShow(
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
