package com.thomaskioko.tvmaniac.data.library.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.storeBuilder
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.usingDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.api.model.getOrThrow
import com.thomaskioko.tvmaniac.data.library.model.LibrarySortOption
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
import com.thomaskioko.tvmaniac.trakt.api.TraktListRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktFollowedShowResponse
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
import kotlin.time.Instant

@Inject
@SingleIn(AppScope::class)
public class LibraryStore(
    private val traktListDataSource: TraktListRemoteDataSource,
    private val tmdbDataSource: TmdbShowDetailsNetworkDataSource,
    private val followedShowsDao: FollowedShowsDao,
    private val tvShowsDao: TvShowsDao,
    private val requestManagerRepository: RequestManagerRepository,
    private val syncRepository: ActivitySyncRepository,
    private val transactionRunner: DatabaseTransactionRunner,
    private val formatterUtil: FormatterUtil,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<LibrarySortOption, List<FollowedShowEntry>> by storeBuilder(
    fetcher = Fetcher.of { key: LibrarySortOption ->
        coroutineScope {
            traktListDataSource.getWatchList(sortBy = key.sortBy, sortHow = key.sortHow)
                .getOrThrow()
                .map { followedShow ->
                    async {
                        when (val tmdb = tmdbDataSource.getShowDetails(followedShow.show.ids.tmdb)) {
                            is ApiResponse.Success -> FollowedShowWithImages(
                                response = followedShow,
                                tmdbPosterPath = tmdb.body.posterPath,
                                tmdbBackdropPath = tmdb.body.backdropPath,
                            )
                            is ApiResponse.Unauthenticated,
                            is ApiResponse.Error,
                            -> FollowedShowWithImages(
                                response = followedShow,
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
                val currentByTraktId = currentEntries.associateBy { it.traktId }
                val networkTraktIds = response.map { it.response.show.ids.trakt }.toSet()

                response.forEach { item ->
                    val entry = item.response.toFollowedShowEntry()
                    val existingEntry = currentByTraktId[entry.traktId]

                    tvShowsDao.upsertMerging(
                        item.response.toTvshow(
                            posterPath = item.tmdbPosterPath?.let { formatterUtil.formatTmdbPosterPath(it) },
                            backdropPath = item.tmdbBackdropPath?.let { formatterUtil.formatTmdbPosterPath(it) },
                        ),
                    )

                    val _ = followedShowsDao.upsert(entry.copy(id = existingEntry?.id ?: 0))
                }

                currentEntries.forEach { localEntry ->
                    if (localEntry.traktId !in networkTraktIds) {
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
    val response: TraktFollowedShowResponse,
    val tmdbPosterPath: String?,
    val tmdbBackdropPath: String?,
)

private fun TraktFollowedShowResponse.toFollowedShowEntry(): FollowedShowEntry = FollowedShowEntry(
    traktId = show.ids.trakt,
    tmdbId = show.ids.tmdb,
    followedAt = Instant.parse(listedAt),
    pendingAction = PendingAction.NOTHING,
)

private fun TraktFollowedShowResponse.toTvshow(posterPath: String?, backdropPath: String?): ShowToPersist = ShowToPersist(
    traktId = Id(show.ids.trakt),
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
