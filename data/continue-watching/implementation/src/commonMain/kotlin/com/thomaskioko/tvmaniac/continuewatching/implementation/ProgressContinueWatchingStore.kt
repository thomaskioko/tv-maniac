package com.thomaskioko.tvmaniac.continuewatching.implementation

import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingDao
import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingEntry
import com.thomaskioko.tvmaniac.core.base.extensions.parallelMap
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.fresh
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.get
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.storeBuilder
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.usingDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.db.TraktId
import com.thomaskioko.tvmaniac.db.Tvshow
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.CONTINUE_WATCHING_SYNC
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.syncactivity.api.TraktActivityRepository
import com.thomaskioko.tvmaniac.syncactivity.api.model.ActivityType
import com.thomaskioko.tvmaniac.trakt.api.TraktSyncRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.TraktUserRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktPlaybackEpisodeResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktWatchedProgressResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktWatchedShowResponse
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.FetcherResult
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.Validator
import kotlin.time.Instant

/**
 * Store backing the documented multi-step Progress path.
 *
 * Fetcher pulls `playback`, `hidden`, and `watched-shows` concurrently to
 * compute the candidate set, then fans out
 * [TraktSyncRemoteDataSource.getShowWatchedProgress] per candidate to resolve
 * authoritative aired/completed counts. Returns `null` on any non-success
 * upstream response so the SoT writer skips and the local table stays intact.
 *
 * SourceOfTruth writer atomically replaces the `trakt_continue_watching`
 * table (delete non-incoming, upsert incoming, seed minimal tvshow rows) and
 * stamps the freshness signal.
 */
@Inject
@SingleIn(AppScope::class)
public class ProgressContinueWatchingStore(
    private val traktSyncDataSource: TraktSyncRemoteDataSource,
    private val traktUserDataSource: TraktUserRemoteDataSource,
    private val continueWatchingDao: ContinueWatchingDao,
    private val tvShowsDao: TvShowsDao,
    private val requestManagerRepository: RequestManagerRepository,
    private val traktActivityRepository: TraktActivityRepository,
    private val datastoreRepository: DatastoreRepository,
    private val transactionRunner: DatabaseTransactionRunner,
    private val dispatchers: AppCoroutineDispatchers,
    private val logger: Logger,
) {

    private val store: Store<Unit, List<ContinueWatchingEntry>> = storeBuilder(
        fetcher = Fetcher.ofResult { _: Unit ->
            when (val entries = fetchProgressEntries()) {
                null -> FetcherResult.Error.Exception(
                    FetcherSkipSignal("Progress fetcher signaled skip; leaving local table unchanged"),
                )
                else -> FetcherResult.Data(entries)
            }
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { _: Unit -> continueWatchingDao.entriesObservable() },
            writer = { _: Unit, entries: List<ContinueWatchingEntry> ->
                val incomingTraktIds = entries.map { it.traktId }.toSet()
                transactionRunner {
                    continueWatchingDao.entries()
                        .filter { it.traktId !in incomingTraktIds }
                        .forEach { continueWatchingDao.deleteByTraktId(it.traktId) }
                    entries.forEach { continueWatchingDao.upsert(it) }
                    entries.forEach { entry -> entry.toMinimalTvshow()?.let(tvShowsDao::upsertMerging) }
                }
                requestManagerRepository.upsert(
                    entityId = CONTINUE_WATCHING_SYNC.requestId,
                    requestType = CONTINUE_WATCHING_SYNC.name,
                )
                traktActivityRepository.markActivityAsSynced(ActivityType.EPISODES_WATCHED)
            },
            delete = { _: Unit -> continueWatchingDao.deleteAll() },
            deleteAll = { continueWatchingDao.deleteAll() },
        ).usingDispatchers(
            readDispatcher = dispatchers.databaseRead,
            writeDispatcher = dispatchers.databaseWrite,
        ),
    ).validator(
        Validator.by {
            withContext(dispatchers.io) {
                val ttlValid = requestManagerRepository.isRequestValid(
                    requestType = CONTINUE_WATCHING_SYNC.name,
                    threshold = CONTINUE_WATCHING_SYNC.duration,
                )
                val activityChanged = traktActivityRepository.hasActivityChanged(ActivityType.EPISODES_WATCHED)
                ttlValid && !activityChanged
            }
        },
    ).build()

    public suspend fun fetchWith(forceRefresh: Boolean) {
        if (forceRefresh) store.fresh(Unit) else store.get(Unit)
    }

    private suspend fun fetchProgressEntries(): List<ContinueWatchingEntry>? = coroutineScope {
        val cursor = traktActivityRepository.getEpisodesWatchedSyncTimeStamp()
        val playbackDeferred = async { traktSyncDataSource.getPlaybackEpisodes() }
        val hiddenDeferred = async { traktUserDataSource.getHiddenProgressWatched() }
        val watchedShowsDeferred = async {
            traktSyncDataSource.getWatchedShows(page = 1, limit = WATCHED_SHOWS_LIMIT)
        }

        val playbackResponse = playbackDeferred.await()
        if (playbackResponse !is ApiResponse.Success) return@coroutineScope null
        val watchedShowsResponse = watchedShowsDeferred.await()
        if (watchedShowsResponse !is ApiResponse.Success) return@coroutineScope null
        val hiddenResponse = hiddenDeferred.await()
        if (hiddenResponse !is ApiResponse.Success) return@coroutineScope null

        val hiddenIds = hiddenResponse.body
            .mapNotNull { it.show?.ids?.trakt }
            .toSet()
        val descriptors = buildDescriptors(watchedShowsResponse.body, playbackResponse.body)
        val candidates = descriptors.keys.filter { it !in hiddenIds }

        val lastActivity = cursor?.toString()
        val includeSpecials = datastoreRepository.getIncludeSpecials()

        val perShowResults = candidates.parallelMap(concurrency = PROGRESS_FAN_OUT_CONCURRENCY) { traktId ->
            traktId to traktSyncDataSource.getShowWatchedProgress(
                traktId = traktId,
                lastActivity = lastActivity,
                specials = includeSpecials,
            )
        }
        val withNextEpisode = perShowResults.count { (_, response) ->
            (response as? ApiResponse.Success)?.body?.nextEpisode != null
        }
        logger.debug(
            LOG_TAG,
            "Progress per-show fan-out: candidates=${candidates.size} withNextEpisode=$withNextEpisode",
        )

        if (perShowResults.any { (_, response) -> response !is ApiResponse.Success }) {
            return@coroutineScope null
        }

        perShowResults.mapNotNull { (traktId, response) ->
            val progress = (response as ApiResponse.Success).body
            // Load-bearing filter. Catches reset shows (`reset_at` populated) and any case where
            // Trakt's per-show logic returns null next_episode. Removing it would persist rows
            // that never render in the watchlist.
            if (progress.nextEpisode == null) return@mapNotNull null
            toEntry(traktId, descriptors[traktId], progress)
        }
    }

    private fun buildDescriptors(
        watchedShows: List<TraktWatchedShowResponse>,
        playback: List<TraktPlaybackEpisodeResponse>,
    ): Map<Long, ProgressDescriptor> = buildMap {
        watchedShows.forEach { item ->
            val show = item.show
            put(show.ids.trakt, ProgressDescriptor(show.ids.tmdb, show.title, show.year))
        }
        playback.forEach { item ->
            put(item.show.ids.trakt, ProgressDescriptor(item.show.ids.tmdb, item.show.title, item.show.year))
        }
    }

    private companion object {
        const val PROGRESS_FAN_OUT_CONCURRENCY = 4
        const val WATCHED_SHOWS_LIMIT = 100
        const val LOG_TAG = "ProgressContinueWatchingStore"
    }
}

private data class ProgressDescriptor(
    val tmdbId: Long?,
    val title: String?,
    val year: Long?,
)

private fun toEntry(
    traktId: Long,
    descriptor: ProgressDescriptor?,
    progress: TraktWatchedProgressResponse,
): ContinueWatchingEntry {
    val lastWatchedAtMs = progress.lastWatchedAt
        ?.let { Instant.parse(it).toEpochMilliseconds() }
        ?: 0L
    return ContinueWatchingEntry(
        traktId = traktId,
        tmdbId = descriptor?.tmdbId,
        airedEpisodes = progress.aired.toLong(),
        completedCount = progress.completed.toLong(),
        lastWatchedAt = lastWatchedAtMs,
        lastUpdatedAt = lastWatchedAtMs,
        title = descriptor?.title,
        year = descriptor?.year,
    )
}

private fun ContinueWatchingEntry.toMinimalTvshow(): Tvshow? {
    val tmdb = tmdbId ?: return null
    val name = title ?: return null
    return Tvshow(
        trakt_id = Id<TraktId>(traktId),
        tmdb_id = Id<TmdbId>(tmdb),
        name = name,
        overview = "",
        language = null,
        year = year?.toString(),
        ratings = 0.0,
        vote_count = 0,
        genres = null,
        status = null,
        episode_numbers = null,
        season_numbers = null,
        poster_path = null,
        backdrop_path = null,
    )
}
