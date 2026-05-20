package com.thomaskioko.tvmaniac.continuewatching.implementation

import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingDao
import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingEntry
import com.thomaskioko.tvmaniac.core.base.extensions.parallelMap
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.syncactivity.api.TraktActivityRepository
import com.thomaskioko.tvmaniac.trakt.api.TraktSyncRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.TraktUserRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktWatchedProgressResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktWatchedShowResponse
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.time.Instant

@ContributesBinding(
    scope = AppScope::class,
    binding = binding<@Progress ContinueWatchingFetcher>(),
)
@SingleIn(AppScope::class)
public class ProgressContinueWatchingFetcher(
    private val traktSyncDataSource: TraktSyncRemoteDataSource,
    private val traktUserDataSource: TraktUserRemoteDataSource,
    private val traktActivityRepository: TraktActivityRepository,
    private val continueWatchingDao: ContinueWatchingDao,
    private val tvShowsDao: TvShowsDao,
    private val transactionRunner: DatabaseTransactionRunner,
    private val logger: Logger,
) : ContinueWatchingFetcher {

    public override suspend fun run(forceRefresh: Boolean): List<ContinueWatchingEntry>? = coroutineScope {
        val cursor = traktActivityRepository.getEpisodesWatchedSyncTimeStamp()
        val playbackDeferred = async { traktSyncDataSource.getPlaybackEpisodes() }
        val hiddenDeferred = async { traktUserDataSource.getHiddenProgressWatched() }
        val watchedShowsDeferred = async { drainWatchedShows() }

        val playbackResponse = playbackDeferred.await()
        if (playbackResponse !is ApiResponse.Success) return@coroutineScope null
        val playback = playbackResponse.body
        val watchedShows = watchedShowsDeferred.await()
        val cachedEntries = continueWatchingDao.entries()
        val hiddenIds = hiddenDeferred.await().bodyOrEmpty()
            .mapNotNull { it.show?.ids?.trakt }
            .toSet()

        val descriptors = buildMap {
            cachedEntries.forEach { entry ->
                put(entry.traktId, ShowDescriptor(entry.tmdbId, entry.title, entry.year))
            }
            watchedShows.forEach { item ->
                val show = item.show
                put(show.ids.trakt, ShowDescriptor(show.ids.tmdb, show.title, show.year))
            }
            playback.forEach { item ->
                put(item.show.ids.trakt, ShowDescriptor(item.show.ids.tmdb, item.show.title, item.show.year))
            }
        }

        val candidates = descriptors.keys
            .filter { it !in hiddenIds }
        val lastActivity = if (forceRefresh) null else cursor?.toString()

        // Provisional write: commit minimal continue_watching + tvshow rows for each candidate
        // before per-show progress completes. This brings the documented path's first-sync UX
        // up to Nitro's: the Watchlist surfaces shows roughly when playback+history return,
        // not after all per-show progress calls finish. Progress fields are placeholder zeros;
        // the Store5 writer below commits accurate values once per-show progress lands.
        // Reset/finished shows that fail the next_episode filter briefly appear here then get
        // removed by the writer's delete-not-in-list pass.
        transactionRunner {
            candidates.forEach { traktId ->
                val descriptor = descriptors[traktId] ?: return@forEach
                val provisional = ContinueWatchingEntry(
                    traktId = traktId,
                    tmdbId = descriptor.tmdbId,
                    airedEpisodes = 0L,
                    completedCount = 0L,
                    lastWatchedAt = 0L,
                    lastUpdatedAt = 0L,
                    title = descriptor.title,
                    year = descriptor.year,
                )
                continueWatchingDao.upsert(provisional)
                provisional.toMinimalTvshow()?.let(tvShowsDao::upsertMerging)
            }
        }

        val perShowResults = candidates.parallelMap(concurrency = CONCURRENCY) { traktId ->
            traktId to traktSyncDataSource.getShowWatchedProgress(
                traktId = traktId,
                lastActivity = lastActivity,
            )
        }
        val withNextEpisode = perShowResults.count { (_, response) ->
            (response as? ApiResponse.Success)?.body?.nextEpisode != null
        }
        val outcomeBreakdown = perShowResults
            .map { (_, response) -> response.outcomeLabel() }
            .groupingBy { it }
            .eachCount()
        logger.debug(
            LOG_TAG,
            "Progress per-show progress fan-out: candidates=${candidates.size} " +
                "withNextEpisode=$withNextEpisode breakdown=$outcomeBreakdown",
        )

        perShowResults.mapNotNull { (traktId, response) ->
            val progress = (response as? ApiResponse.Success)?.body
                ?: return@mapNotNull null
            // Load-bearing filter. Catches reset shows (`reset_at` populated) and any case where
            // Trakt's per-show logic returns null next_episode. Removing it would persist rows
            // that never render in the watchlist.
            if (progress.nextEpisode == null) return@mapNotNull null
            toEntry(traktId, descriptors[traktId], progress)
        }
    }

    private suspend fun drainWatchedShows(): List<TraktWatchedShowResponse> {
        val collected = mutableListOf<TraktWatchedShowResponse>()
        var page = 1
        while (true) {
            val response = traktSyncDataSource.getWatchedShows(page = page, limit = WATCHED_SHOWS_PAGE_SIZE)
            val body = (response as? ApiResponse.Success)?.body ?: break
            if (body.isEmpty()) break
            collected += body
            if (body.size < WATCHED_SHOWS_PAGE_SIZE) break
            page++
        }
        return collected
    }

    private companion object {
        const val CONCURRENCY = 4
        const val WATCHED_SHOWS_PAGE_SIZE = 100
        const val LOG_TAG = "ProgressContinueWatchingFetcher"
    }
}

private fun <T> ApiResponse<List<T>>.bodyOrEmpty(): List<T> =
    (this as? ApiResponse.Success)?.body ?: emptyList()

private fun ApiResponse<*>.outcomeLabel(): String = when (this) {
    is ApiResponse.Success -> "ok"
    is ApiResponse.Error.HttpError -> "http_$code"
    is ApiResponse.Error.NetworkFailure -> "network_$kind"
    is ApiResponse.Error.OfflineError -> "offline"
    is ApiResponse.Error.SerializationError -> "serialization"
    is ApiResponse.Unauthenticated -> "unauthenticated"
}

private data class ShowDescriptor(
    val tmdbId: Long?,
    val title: String?,
    val year: Long?,
)

private fun toEntry(
    traktId: Long,
    descriptor: ShowDescriptor?,
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
