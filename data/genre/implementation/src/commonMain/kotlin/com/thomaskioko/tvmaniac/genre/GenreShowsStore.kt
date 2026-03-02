package com.thomaskioko.tvmaniac.genre

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.storeBuilder
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.usingDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.api.model.getOrThrow
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Tvshow
import com.thomaskioko.tvmaniac.genre.model.GenreShowCategory
import com.thomaskioko.tvmaniac.genre.model.GenreShowsStoreKey
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.GENRE_SHOWS
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

@Inject
public class GenreShowsStore(
    private val traktRemoteDataSource: TraktShowsRemoteDataSource,
    private val tmdbDetailsDataSource: TmdbShowDetailsNetworkDataSource,
    private val traktGenreDao: TraktGenreDao,
    private val tvShowsDao: TvShowsDao,
    private val requestManagerRepository: RequestManagerRepository,
    private val formatterUtil: FormatterUtil,
    private val dateTimeProvider: DateTimeProvider,
    private val databaseTransactionRunner: DatabaseTransactionRunner,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<GenreShowsStoreKey, List<ShowEntity>> by storeBuilder(
    fetcher = Fetcher.of { key: GenreShowsStoreKey ->
        coroutineScope {
            val traktShows: List<TraktShowResponse> = when (key.category) {
                GenreShowCategory.POPULAR ->
                    traktRemoteDataSource.getPopularShows(page = 1, limit = 10, genres = key.genreSlug)
                        .getOrThrow()
                GenreShowCategory.TRENDING ->
                    traktRemoteDataSource.getTrendingShows(page = 1, limit = 10, genres = key.genreSlug)
                        .getOrThrow()
                        .map { it.show }
                GenreShowCategory.TOP_RATED ->
                    traktRemoteDataSource.getFavoritedShows(page = 1, limit = 10, genres = key.genreSlug)
                        .getOrThrow()
                        .map { it.show }
                GenreShowCategory.MOST_WATCHED ->
                    traktRemoteDataSource.getMostWatchedShows(page = 1, limit = 10, genres = key.genreSlug)
                        .getOrThrow()
                        .map { it.show }
            }

            traktShows
                .withIndex()
                .mapNotNull { (index, show) ->
                    val tmdbId = show.ids.tmdb ?: return@mapNotNull null
                    async {
                        when (val tmdbDetails = tmdbDetailsDataSource.getShowDetails(tmdbId)) {
                            is ApiResponse.Success -> GenreShowWithImages(
                                traktShow = show,
                                tmdbId = tmdbId,
                                tmdbPosterPath = tmdbDetails.body.posterPath,
                                tmdbBackdropPath = tmdbDetails.body.backdropPath,
                                pageOrder = index,
                            )

                            is ApiResponse.Error -> GenreShowWithImages(
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
    sourceOfTruth = SourceOfTruth.of<GenreShowsStoreKey, List<GenreShowWithImages>, List<ShowEntity>>(
        reader = { key -> traktGenreDao.observeShowsByGenreSlugAndCategory(key.genreSlug, key.category.name) },
        writer = { key, response ->
            withContext(dispatchers.databaseWrite) {
                databaseTransactionRunner {
                    traktGenreDao.deleteShowsByGenreSlugAndCategory(key.genreSlug, key.category.name)
                    requestManagerRepository.upsert(
                        entityId = "${key.genreSlug}_${key.category.name}".hashCode().toLong(),
                        requestType = GENRE_SHOWS.name,
                    )

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

                        tvShowsDao.upsertMerging(
                            show.toTvshow(traktId, tmdbId, posterPath, backdropPath, dateTimeProvider),
                        )

                        traktGenreDao.upsertGenreShow(
                            genreSlug = key.genreSlug,
                            traktId = traktId,
                            pageOrder = showWithImages.pageOrder.toLong(),
                            category = key.category.name,
                        )
                    }
                }
            }
        },
        delete = { key -> traktGenreDao.deleteShowsByGenreSlugAndCategory(key.genreSlug, key.category.name) },
        deleteAll = { },
    ).usingDispatchers(
        readDispatcher = dispatchers.databaseRead,
        writeDispatcher = dispatchers.databaseWrite,
    ),
).build()

internal data class GenreShowWithImages(
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
