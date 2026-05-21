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
import com.thomaskioko.tvmaniac.trakt.api.model.TraktWatchedProgressResponse
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.withContext
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.FetcherResult
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.Validator
import kotlin.time.Instant

@Inject
@SingleIn(AppScope::class)
public class ContinueWatchingStore(
    private val nitroFetcher: NitroContinueWatchingFetcher,
    private val traktSyncDataSource: TraktSyncRemoteDataSource,
    private val continueWatchingDao: ContinueWatchingDao,
    private val tvShowsDao: TvShowsDao,
    private val requestManagerRepository: RequestManagerRepository,
    private val traktActivityRepository: TraktActivityRepository,
    private val datastoreRepository: DatastoreRepository,
    private val transactionRunner: DatabaseTransactionRunner,
    private val dispatchers: AppCoroutineDispatchers,
    private val logger: Logger,
) {

    private val store: Store<ContinueWatchingKey, List<ContinueWatchingEntry>> = storeBuilder(
        fetcher = Fetcher.ofResult { key: ContinueWatchingKey ->
            val forceRefresh = currentCoroutineContext()[FetchHints]?.forceRefresh ?: false
            val entries = when (key) {
                ContinueWatchingKey.Progress -> runProgressFanOut(forceRefresh)
                ContinueWatchingKey.Nitro -> nitroFetcher.run(forceRefresh)
            }
            when (entries) {
                null -> FetcherResult.Error.Exception(
                    FetcherSkipSignal("Fetcher signaled skip; leaving local table unchanged"),
                )
                else -> FetcherResult.Data(entries)
            }
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { _: ContinueWatchingKey -> continueWatchingDao.entriesObservable() },
            writer = { _: ContinueWatchingKey, entries: List<ContinueWatchingEntry> ->
                val incomingTraktIds = entries.map { it.traktId }.toSet()
                transactionRunner {
                    val existing = continueWatchingDao.entries()
                    existing
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
            delete = { _: ContinueWatchingKey -> continueWatchingDao.deleteAll() },
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

    /**
     * Triggers a Store5 fetch or cache read depending on [forceRefresh]. The
     * [forceRefresh] flag is plumbed to the fetcher lambda via [FetchHints] on
     * the coroutine context so the key can stay free of call-time state.
     *
     * Throws [FetcherSkipSignal] (via Store5's error propagation) when the
     * Progress fan-out or Nitro fetcher returns null. Callers should catch it
     * explicitly if they want "skip the write" to be silent.
     */
    public suspend fun fetchWith(key: ContinueWatchingKey, forceRefresh: Boolean) {
        withContext(FetchHints(forceRefresh)) {
            if (forceRefresh) {
                store.fresh(key)
            } else {
                store.get(key)
            }
        }
    }

    /**
     * Per-show progress fan-out for the Progress key.
     *
     * Reads the candidate set that [ContinueWatchingDiscoveryStore] has
     * already placed in the DAO and resolves authoritative counts via
     * [TraktSyncRemoteDataSource.getShowWatchedProgress]. Returns `null` on
     * any non-success response so the SoT writer skips and the local table
     * stays intact.
     */
    private suspend fun runProgressFanOut(forceRefresh: Boolean): List<ContinueWatchingEntry>? {
        val daoRows = continueWatchingDao.entries()
        if (daoRows.isEmpty()) {
            logger.debug(LOG_TAG, "No candidates in DAO; nothing to fan out.")
            return emptyList()
        }

        val cursor = traktActivityRepository.getEpisodesWatchedSyncTimeStamp()
        val lastActivity = if (forceRefresh) null else cursor?.toString()
        val includeSpecials = datastoreRepository.getIncludeSpecials()

        val descriptorByTraktId = daoRows.associateBy { it.traktId }
        val candidates = descriptorByTraktId.keys.toList()

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
            return null
        }

        return perShowResults.mapNotNull { (traktId, response) ->
            val progress = (response as ApiResponse.Success).body
            // Load-bearing filter. Catches reset shows (`reset_at` populated) and any case where
            // Trakt's per-show logic returns null next_episode. Removing it would persist rows
            // that never render in the watchlist.
            if (progress.nextEpisode == null) return@mapNotNull null
            toEntry(traktId, descriptorByTraktId[traktId], progress)
        }
    }

    private companion object {
        const val PROGRESS_FAN_OUT_CONCURRENCY = 4
        const val LOG_TAG = "ContinueWatchingStore"
    }
}

internal fun ContinueWatchingEntry.toMinimalTvshow(): Tvshow? {
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

private fun toEntry(
    traktId: Long,
    existing: ContinueWatchingEntry?,
    progress: TraktWatchedProgressResponse,
): ContinueWatchingEntry {
    val lastWatchedAtMs = progress.lastWatchedAt
        ?.let { Instant.parse(it).toEpochMilliseconds() }
        ?: 0L
    return ContinueWatchingEntry(
        traktId = traktId,
        tmdbId = existing?.tmdbId,
        airedEpisodes = progress.aired.toLong(),
        completedCount = progress.completed.toLong(),
        lastWatchedAt = lastWatchedAtMs,
        lastUpdatedAt = lastWatchedAtMs,
        title = existing?.title,
        year = existing?.year,
    )
}
