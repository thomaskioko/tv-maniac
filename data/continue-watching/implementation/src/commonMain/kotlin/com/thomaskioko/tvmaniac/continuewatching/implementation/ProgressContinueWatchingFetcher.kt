package com.thomaskioko.tvmaniac.continuewatching.implementation

import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingDao
import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingEntry
import com.thomaskioko.tvmaniac.core.base.extensions.parallelMap
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.db.TraktId
import com.thomaskioko.tvmaniac.db.Tvshow
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.syncactivity.api.TraktActivityRepository
import com.thomaskioko.tvmaniac.trakt.api.TraktSyncRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.TraktUserRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktPlaybackEpisodeResponse
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
    private val datastoreRepository: DatastoreRepository,
    private val logger: Logger,
) : ContinueWatchingFetcher {

    public override suspend fun run(forceRefresh: Boolean): List<ContinueWatchingEntry>? = coroutineScope {
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

        // Skeleton seed: new candidates get a placeholder row so the watchlist surfaces them
        // immediately with title/year. Existing rows are left alone (INSERT OR IGNORE), so their
        // real counts survive the refresh. The Store writer overwrites with authoritative data
        // once the per-show fan-out below completes.
        transactionRunner {
            candidates.forEach { traktId ->
                val descriptor = descriptors[traktId] ?: return@forEach
                continueWatchingDao.upsertPlaceholder(
                    traktId = traktId,
                    tmdbId = descriptor.tmdbId,
                    title = descriptor.title,
                    year = descriptor.year,
                )
                descriptor.toMinimalTvshow(traktId)?.let(tvShowsDao::upsertMerging)
            }
        }

        val lastActivity = if (forceRefresh) null else cursor?.toString()
        val includeSpecials = datastoreRepository.getIncludeSpecials()

        val perShowResults = candidates.parallelMap(concurrency = CONCURRENCY) { traktId ->
            traktId to traktSyncDataSource.getShowWatchedProgress(
                traktId = traktId,
                lastActivity = lastActivity,
                specials = includeSpecials,
            )
        }
        val outcomeBreakdown = perShowResults
            .groupingBy { it }
            .eachCount()
        val withNextEpisode = perShowResults.count { (_, response) ->
            (response as? ApiResponse.Success)?.body?.nextEpisode != null
        }
        logger.debug(
            LOG_TAG,
            "Progress per-show progress fan-out: candidates=${candidates.size} " +
                "withNextEpisode=$withNextEpisode breakdown=$outcomeBreakdown",
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
    ): Map<Long, ShowDescriptor> = buildMap {
        watchedShows.forEach { item ->
            val show = item.show
            put(show.ids.trakt, ShowDescriptor(show.ids.tmdb, show.title, show.year))
        }
        playback.forEach { item ->
            put(item.show.ids.trakt, ShowDescriptor(item.show.ids.tmdb, item.show.title, item.show.year))
        }
    }

    private companion object {
        const val CONCURRENCY = 4

        // Single-page bootstrap of watched shows. Trakt's `/sync/watched/shows` is sorted by
        // last_watched desc, so the most-recently-watched 100 shows are more than enough
        // candidates: older entries either have no `nextEpisode` (filtered out anyway) or are
        // already surfaced via `playback`.
        const val WATCHED_SHOWS_LIMIT = 100
        const val LOG_TAG = "ProgressContinueWatchingFetcher"
    }
}

private data class ShowDescriptor(
    val tmdbId: Long?,
    val title: String?,
    val year: Long?,
)

private fun ShowDescriptor.toMinimalTvshow(traktId: Long): Tvshow? {
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
