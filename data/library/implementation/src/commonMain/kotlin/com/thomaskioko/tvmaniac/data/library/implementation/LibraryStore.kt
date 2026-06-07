package com.thomaskioko.tvmaniac.data.library.implementation

import com.thomaskioko.tvmaniac.connectedaccount.api.ConnectedAccountRepository
import com.thomaskioko.tvmaniac.connectedaccount.api.getActiveProvider
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.storeBuilder
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.usingDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.api.model.AuthenticationException
import com.thomaskioko.tvmaniac.core.networkutil.api.model.getOrThrow
import com.thomaskioko.tvmaniac.data.library.LibraryRemoteDataSource
import com.thomaskioko.tvmaniac.data.library.model.LibrarySortOption
import com.thomaskioko.tvmaniac.data.library.model.RemoteFollowedShow
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowEntry
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsDao
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.LIBRARY_SYNC
import com.thomaskioko.tvmaniac.shows.api.ShowToPersist
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.syncactivity.api.ActivitySyncRepository
import com.thomaskioko.tvmaniac.syncactivity.api.ActivitySyncTypes
import com.thomaskioko.tvmaniac.syncactivity.api.model.ActivityType.SHOWS_WATCHLISTED
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowDetailsNetworkDataSource
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
public class LibraryStore(
    private val sources: Set<LibraryRemoteDataSource>,
    private val connectedAccountRepository: ConnectedAccountRepository,
    private val tmdbDataSource: TmdbShowDetailsNetworkDataSource,
    private val followedShowsDao: FollowedShowsDao,
    private val tvShowsDao: TvShowsDao,
    private val requestManagerRepository: RequestManagerRepository,
    private val syncRepository: ActivitySyncRepository,
    private val transactionRunner: DatabaseTransactionRunner,
    private val formatterUtil: FormatterUtil,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<LibrarySortOption, List<FollowedShowEntry>> by storeBuilder(
    fetcher = Fetcher.of { _: LibrarySortOption ->
        coroutineScope {
            val source = sources.getActiveProvider(connectedAccountRepository)
                ?: throw AuthenticationException("No active sync provider")
            source.getWatchlist()
                .getOrThrow()
                .map { watchlistShow ->
                    async {
                        when (val tmdb = tmdbDataSource.getShowDetails(watchlistShow.tmdbId)) {
                            is ApiResponse.Success -> FollowedShowWithImages(
                                show = watchlistShow,
                                tmdbPosterPath = tmdb.body.posterPath,
                                tmdbBackdropPath = tmdb.body.backdropPath,
                            )
                            is ApiResponse.Unauthenticated,
                            is ApiResponse.Error,
                            -> FollowedShowWithImages(
                                show = watchlistShow,
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
        reader = { _: LibrarySortOption -> followedShowsDao.entriesObservable() },
        writer = { _: LibrarySortOption, response: List<FollowedShowWithImages> ->
            transactionRunner {
                val currentEntries = followedShowsDao.entriesWithNoPendingAction()
                val networkShowIds = response.map { it.show.showId }.toSet()

                response.forEach { item ->
                    val entry = item.show.toFollowedShowEntry()

                    tvShowsDao.upsertMerging(
                        item.show.toTvshow(
                            posterPath = item.tmdbPosterPath?.let { formatterUtil.formatTmdbPosterPath(it) },
                            backdropPath = item.tmdbBackdropPath?.let { formatterUtil.formatTmdbPosterPath(it) },
                        ),
                    )

                    val _ = followedShowsDao.upsert(entry)
                }

                currentEntries.forEach { localEntry ->
                    if (localEntry.showId !in networkShowIds) {
                        followedShowsDao.deleteById(localEntry.id)
                    }
                }
            }

            requestManagerRepository.upsert(
                entityId = LIBRARY_SYNC.requestId,
                requestType = LIBRARY_SYNC.name,
            )

            syncRepository.markSyncedTo(
                consumerId = ActivitySyncTypes.LIBRARY_WATCHLIST,
                activityType = SHOWS_WATCHLISTED,
            )
        },
        delete = { _: LibrarySortOption -> },
        deleteAll = { },
    ).usingDispatchers(
        readDispatcher = dispatchers.databaseRead,
        writeDispatcher = dispatchers.databaseWrite,
    ),
).validator(
    Validator.by {
        withContext(dispatchers.io) {
            requestManagerRepository.isRequestValid(
                requestType = LIBRARY_SYNC.name,
                threshold = LIBRARY_SYNC.duration,
            )
        }
    },
).build()

private data class FollowedShowWithImages(
    val show: RemoteFollowedShow,
    val tmdbPosterPath: String?,
    val tmdbBackdropPath: String?,
)

private fun RemoteFollowedShow.toFollowedShowEntry(): FollowedShowEntry = FollowedShowEntry(
    showId = showId,
    tmdbId = tmdbId,
    followedAt = followedAt,
    pendingAction = PendingAction.NOTHING,
)

private fun RemoteFollowedShow.toTvshow(posterPath: String?, backdropPath: String?): ShowToPersist = ShowToPersist(
    showId = Id(showId),
    tmdbId = Id(tmdbId),
    name = title,
    overview = "",
    language = null,
    year = year?.toString(),
    status = null,
    ratings = 0.0,
    voteCount = 0,
    genres = null,
    posterPath = posterPath,
    backdropPath = backdropPath,
    episodeNumbers = null,
    seasonNumbers = null,
)
