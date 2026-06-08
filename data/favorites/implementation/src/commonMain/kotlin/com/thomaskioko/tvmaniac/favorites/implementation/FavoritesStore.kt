package com.thomaskioko.tvmaniac.favorites.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.storeBuilder
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.usingDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.api.model.getOrThrow
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.favorites.api.FavoriteShow
import com.thomaskioko.tvmaniac.favorites.api.FavoritesDao
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.FAVORITES_SYNC
import com.thomaskioko.tvmaniac.shows.api.ShowToPersist
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowDetailsNetworkDataSource
import com.thomaskioko.tvmaniac.trakt.api.TraktUserRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktFavoriteShowResponse
import com.thomaskioko.tvmaniac.util.api.FormatterUtil
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.Validator

@Inject
@SingleIn(AppScope::class)
public class FavoritesStore(
    private val traktUserDataSource: TraktUserRemoteDataSource,
    private val tmdbDataSource: TmdbShowDetailsNetworkDataSource,
    private val favoritesDao: FavoritesDao,
    private val tvShowsDao: TvShowsDao,
    private val requestManagerRepository: RequestManagerRepository,
    private val transactionRunner: DatabaseTransactionRunner,
    private val formatterUtil: FormatterUtil,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<Unit, List<FavoriteShow>> by storeBuilder(
    fetcher = Fetcher.of { _: Unit ->
        coroutineScope {
            traktUserDataSource.getFavoriteShows(userId = "me")
                .getOrThrow()
                .map { favorite ->
                    async {
                        when (val tmdb = tmdbDataSource.getShowDetails(favorite.show.ids.tmdb)) {
                            is ApiResponse.Success -> FavoriteWithImages(
                                response = favorite,
                                tmdbPosterPath = tmdb.body.posterPath,
                                tmdbBackdropPath = tmdb.body.backdropPath,
                            )
                            is ApiResponse.Unauthenticated,
                            is ApiResponse.Error,
                            -> FavoriteWithImages(
                                response = favorite,
                                tmdbPosterPath = null,
                                tmdbBackdropPath = null,
                            )
                        }
                    }
                }
                .awaitAll()
        }
    },
    sourceOfTruth = SourceOfTruth.of(
        reader = { _: Unit -> favoritesDao.observeFavoriteShows() },
        writer = { _: Unit, response: List<FavoriteWithImages> ->
            transactionRunner {
                favoritesDao.deleteAll()
                response.forEach { item ->
                    tvShowsDao.upsertMerging(
                        item.response.toTvshow(
                            posterPath = item.tmdbPosterPath?.let { formatterUtil.formatTmdbPosterPath(it) },
                            backdropPath = item.tmdbBackdropPath?.let { formatterUtil.formatTmdbPosterPath(it) },
                        ),
                    )
                    favoritesDao.upsert(
                        showId = item.response.show.ids.trakt,
                        rank = item.response.rank.toLong(),
                        listedAt = item.response.listedAt,
                    )
                }
            }

            requestManagerRepository.upsert(
                entityId = FAVORITES_SYNC.requestId,
                requestType = FAVORITES_SYNC.name,
            )
        },
        delete = { _: Unit -> },
        deleteAll = { },
    ).usingDispatchers(
        readDispatcher = dispatchers.databaseRead,
        writeDispatcher = dispatchers.databaseWrite,
    ),
).validator(
    Validator.by {
        withContext(dispatchers.io) {
            requestManagerRepository.isRequestValid(
                requestType = FAVORITES_SYNC.name,
                threshold = FAVORITES_SYNC.duration,
            )
        }
    },
).build()

private data class FavoriteWithImages(
    val response: TraktFavoriteShowResponse,
    val tmdbPosterPath: String?,
    val tmdbBackdropPath: String?,
)

private fun TraktFavoriteShowResponse.toTvshow(posterPath: String?, backdropPath: String?): ShowToPersist = ShowToPersist(
    showId = Id(show.ids.trakt),
    tmdbId = Id(show.ids.tmdb),
    name = show.title,
    overview = "",
    language = null,
    year = show.year?.toString(),
    status = null,
    ratings = 0.0,
    voteCount = 0,
    genres = null,
    posterPath = posterPath,
    backdropPath = backdropPath,
    episodeNumbers = null,
    seasonNumbers = null,
)
