package com.thomaskioko.tvmaniac.continuewatching.implementation

import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingEntry
import com.thomaskioko.tvmaniac.core.base.extensions.parallelMap
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
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
) : ContinueWatchingFetcher {

    public override suspend fun run(forceRefresh: Boolean): List<ContinueWatchingEntry>? = coroutineScope {
        val cursor = traktActivityRepository.getEpisodesWatchedSyncTimeStamp()
        val watchedDeferred = async { traktSyncDataSource.getWatchedShows() }
        val hiddenDeferred = async { traktUserDataSource.getHiddenProgressWatched() }

        val watchedResponse = watchedDeferred.await()
        if (watchedResponse !is ApiResponse.Success) return@coroutineScope null
        val watched = watchedResponse.body
        val hiddenIds = hiddenDeferred.await().bodyOrEmpty()
            .mapNotNull { it.show?.ids?.trakt }
            .toSet()

        // `plays` is the lifetime play count (re-watches included). A re-watcher
        // with `plays >= aired_episodes` falls out of the candidate set here even
        // though the per-show progress call's `nextEpisode != null` check would
        // have correctly classified them as "still has unwatched episodes" had
        // they reached it. Using `plays` is the only signal available before the
        // bulk call returns, so this is the project's accepted approximation.
        val candidates = watched.filter { row ->
            val aired = row.show.airedEpisodes ?: return@filter false
            row.plays < aired && row.show.ids.trakt !in hiddenIds
        }
        val lastActivity = if (forceRefresh) null else cursor?.toString()

        candidates.parallelMap(concurrency = CONCURRENCY) { candidate ->
            candidate to traktSyncDataSource.getShowWatchedProgress(
                traktId = candidate.show.ids.trakt,
                lastActivity = lastActivity,
            )
        }.mapNotNull { (candidate, response) ->
            val progress = (response as? ApiResponse.Success)?.body
                ?: return@mapNotNull null
            // Load-bearing filter. Catches reset shows (`reset_at` populated),
            // specials-filter races, and server-side computation drift where
            // `plays < aired` but Trakt's per-show logic returns null next_episode.
            // Removing it would persist rows that never render in the watchlist
            // and waste a per-show progress call.
            if (progress.nextEpisode == null) return@mapNotNull null
            candidate.toEntry(progress)
        }
    }

    private companion object {
        const val CONCURRENCY = 4
    }
}

private fun <T> ApiResponse<List<T>>.bodyOrEmpty(): List<T> =
    (this as? ApiResponse.Success)?.body ?: emptyList()

private fun TraktWatchedShowResponse.toEntry(
    progress: TraktWatchedProgressResponse,
): ContinueWatchingEntry = ContinueWatchingEntry(
    traktId = show.ids.trakt,
    tmdbId = show.ids.tmdb,
    airedEpisodes = (show.airedEpisodes ?: 0L),
    completedCount = progress.completed.toLong(),
    lastWatchedAt = Instant.parse(lastWatchedAt).toEpochMilliseconds(),
    lastUpdatedAt = Instant.parse(lastUpdatedAt).toEpochMilliseconds(),
)
