package com.thomaskioko.tvmaniac.continuewatching.implementation

import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingEntry
import com.thomaskioko.tvmaniac.core.base.extensions.parallelMap
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.syncactivity.api.TraktActivityRepository
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
import kotlin.time.Instant

@Inject
@SingleIn(AppScope::class)
public class ProgressContinueWatchingFetcher(
    private val traktSyncDataSource: TraktSyncRemoteDataSource,
    private val traktUserDataSource: TraktUserRemoteDataSource,
    private val traktActivityRepository: TraktActivityRepository,
    private val datastoreRepository: DatastoreRepository,
    private val logger: Logger,
) {

    public suspend operator fun invoke(): List<ContinueWatchingEntry>? = coroutineScope {
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
        const val LOG_TAG = "ProgressContinueWatchingFetcher"
    }
}

internal data class ProgressDescriptor(
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
