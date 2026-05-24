package com.thomaskioko.tvmaniac.continuewatching.implementation

import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingEntry
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.api.model.SyncError
import com.thomaskioko.tvmaniac.core.networkutil.api.model.toSyncError
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.syncactivity.api.ActivitySyncTypes
import com.thomaskioko.tvmaniac.syncactivity.api.ActivitySyncRepository
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlin.time.Instant

@Inject
@SingleIn(AppScope::class)
public class ProgressContinueWatchingFetcher(
    private val traktSyncDataSource: TraktSyncRemoteDataSource,
    private val traktUserDataSource: TraktUserRemoteDataSource,
    private val syncRepository: ActivitySyncRepository,
    private val datastoreRepository: DatastoreRepository,
    private val logger: Logger,
) {

    internal operator fun invoke(): Flow<ProgressBatch?> = channelFlow {
        val cursor = syncRepository.getSyncTimestamp(
            consumerId = ActivitySyncTypes.PROGRESS_CONTINUE_WATCHING,
            activityType = ActivityType.EPISODES_WATCHED,
        )
        val playbackDeferred = async { traktSyncDataSource.getPlaybackEpisodes() }
        val hiddenDeferred = async { traktUserDataSource.getHiddenProgressWatched() }
        val watchedShowsDeferred = async {
            traktSyncDataSource.getWatchedShows(page = 1, limit = WATCHED_SHOWS_LIMIT)
        }

        val playbackResponse = playbackDeferred.await()
        if (playbackResponse !is ApiResponse.Success) {
            send(null)
            return@channelFlow
        }
        val watchedShowsResponse = watchedShowsDeferred.await()
        if (watchedShowsResponse !is ApiResponse.Success) {
            send(null)
            return@channelFlow
        }
        val hiddenResponse = hiddenDeferred.await()
        if (hiddenResponse !is ApiResponse.Success) {
            send(null)
            return@channelFlow
        }

        val hiddenIds = hiddenResponse.body
            .mapNotNull { it.show?.ids?.trakt }
            .toSet()
        val descriptors = buildDescriptors(watchedShowsResponse.body, playbackResponse.body)
        val candidates = descriptors.keys.filter { it !in hiddenIds }

        val lastActivity = cursor?.toString()
        val includeSpecials = datastoreRepository.getIncludeSpecials()

        val responses = mutableListOf<Pair<Long, ApiResponse<TraktWatchedProgressResponse>>>()
        var rateLimitedAt: Long? = null
        for (traktId in candidates) {
            val response = traktSyncDataSource.getShowWatchedProgress(
                traktId = traktId,
                lastActivity = lastActivity,
                specials = includeSpecials,
            )
            responses += traktId to response
            when (response) {
                is ApiResponse.Success -> {
                    val progress = response.body
                    // Load-bearing filter. Catches reset shows (`reset_at` populated) and any case where
                    // Trakt's per-show logic returns null next_episode. Removing it would persist rows
                    // that never render in the watchlist.
                    if (progress.nextEpisode != null) {
                        send(ProgressBatch.Entry(toEntry(traktId, descriptors[traktId], progress)))
                    }
                }
                is ApiResponse.Error -> {
                    if (response.toSyncError() is SyncError.Retryable) {
                        rateLimitedAt = traktId
                        break
                    }
                }
                is ApiResponse.Unauthenticated -> {
                    rateLimitedAt = traktId
                    break
                }
            }
        }

        if (rateLimitedAt != null) {
            logger.warning(LOG_TAG, "Backing off progress fan-out after retryable failure on $rateLimitedAt")
            return@channelFlow
        }

        val withNextEpisode = responses.count { (_, response) ->
            (response as? ApiResponse.Success)?.body?.nextEpisode != null
        }
        logger.debug(
            LOG_TAG,
            "Progress per-show fan-out: candidates=${candidates.size} withNextEpisode=$withNextEpisode",
        )

        if (responses.all { (_, response) -> response is ApiResponse.Success }) {
            val finalTraktIds = responses
                .mapNotNull { (traktId, response) ->
                    val progress = (response as ApiResponse.Success).body
                    if (progress.nextEpisode != null) traktId else null
                }
                .toSet()
            send(ProgressBatch.Complete(finalTraktIds))
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
        const val WATCHED_SHOWS_LIMIT = 100
        const val LOG_TAG = "ProgressContinueWatchingFetcher"
    }
}

internal data class ProgressDescriptor(
    val tmdbId: Long?,
    val title: String?,
    val year: Long?,
)

/**
 * Incremental sync event emitted by Continue Watching fetchers.
 *
 * Subscribers apply each variant to the local table independently, so the UI updates
 * progressively as entries arrive instead of waiting for a single atomic flip.
 */
internal sealed interface ProgressBatch {
    /** Single resolved show entry, sent as soon as it is ready. */
    data class Entry(val entry: ContinueWatchingEntry) : ProgressBatch

    /**
     * Terminal event. [finalTraktIds] is the authoritative trakt-id set for the synced
     * watchlist; subscribers delete rows not in this set, then stamp freshness markers.
     */
    data class Complete(val finalTraktIds: Set<Long>) : ProgressBatch
}

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
