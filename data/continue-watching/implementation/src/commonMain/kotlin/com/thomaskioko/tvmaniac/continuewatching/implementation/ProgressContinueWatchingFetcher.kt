package com.thomaskioko.tvmaniac.continuewatching.implementation

import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingDao
import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingEntry
import com.thomaskioko.tvmaniac.core.base.extensions.parallelMap
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.syncactivity.api.TraktActivityRepository
import com.thomaskioko.tvmaniac.trakt.api.TraktSyncRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.TraktUserRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktWatchedProgressResponse
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
) : ContinueWatchingFetcher {

    public override suspend fun run(forceRefresh: Boolean): List<ContinueWatchingEntry>? = coroutineScope {
        val cursor = traktActivityRepository.getEpisodesWatchedSyncTimeStamp()
        val playbackDeferred = async { traktSyncDataSource.getPlaybackEpisodes() }
        val hiddenDeferred = async { traktUserDataSource.getHiddenProgressWatched() }

        val playbackResponse = playbackDeferred.await()
        if (playbackResponse !is ApiResponse.Success) return@coroutineScope null
        val playback = playbackResponse.body
        val cachedEntries = continueWatchingDao.entries()
        val hiddenIds = hiddenDeferred.await().bodyOrEmpty()
            .mapNotNull { it.show?.ids?.trakt }
            .toSet()

        // tmdbId source of truth per traktId: prefer the freshly returned playback show, fall back to
        // the cached entry. Cached entries cover shows that finished an episode cleanly and have left
        // the playback feed; without the cache they would silently drop until the next playback event.
        val tmdbByTraktId = buildMap {
            cachedEntries.forEach { put(it.traktId, it.tmdbId) }
            playback.forEach { put(it.show.ids.trakt, it.show.ids.tmdb) }
        }

        val candidates = tmdbByTraktId.keys
            .filter { it !in hiddenIds }
        val lastActivity = if (forceRefresh) null else cursor?.toString()

        candidates.parallelMap(concurrency = CONCURRENCY) { traktId ->
            traktId to traktSyncDataSource.getShowWatchedProgress(
                traktId = traktId,
                lastActivity = lastActivity,
            )
        }.mapNotNull { (traktId, response) ->
            val progress = (response as? ApiResponse.Success)?.body
                ?: return@mapNotNull null
            // Load-bearing filter. Catches reset shows (`reset_at` populated) and any case where
            // Trakt's per-show logic returns null next_episode. Removing it would persist rows
            // that never render in the watchlist.
            if (progress.nextEpisode == null) return@mapNotNull null
            toEntry(traktId, tmdbByTraktId[traktId], progress)
        }
    }

    private companion object {
        const val CONCURRENCY = 4
    }
}

private fun <T> ApiResponse<List<T>>.bodyOrEmpty(): List<T> =
    (this as? ApiResponse.Success)?.body ?: emptyList()

private fun toEntry(
    traktId: Long,
    tmdbId: Long?,
    progress: TraktWatchedProgressResponse,
): ContinueWatchingEntry {
    val lastWatchedAtMs = progress.lastWatchedAt
        ?.let { Instant.parse(it).toEpochMilliseconds() }
        ?: 0L
    return ContinueWatchingEntry(
        traktId = traktId,
        tmdbId = tmdbId,
        airedEpisodes = progress.aired.toLong(),
        completedCount = progress.completed.toLong(),
        lastWatchedAt = lastWatchedAtMs,
        lastUpdatedAt = lastWatchedAtMs,
    )
}
