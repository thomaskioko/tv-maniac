package com.thomaskioko.tvmaniac.continuewatching.implementation

import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingEntry
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.CONTINUE_WATCHING_SYNC
import com.thomaskioko.tvmaniac.syncactivity.api.TraktActivityRepository
import com.thomaskioko.tvmaniac.trakt.api.TraktSyncRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.TraktUserRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUpNextNitroResponse
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.time.Instant

@Inject
@SingleIn(AppScope::class)
public class NitroContinueWatchingFetcher(
    private val traktSyncDataSource: TraktSyncRemoteDataSource,
    private val traktUserDataSource: TraktUserRemoteDataSource,
    private val traktActivityRepository: TraktActivityRepository,
    private val dateTimeProvider: DateTimeProvider,
    private val logger: Logger,
) {

    public suspend operator fun invoke(): List<ContinueWatchingEntry>? = coroutineScope {
        val instant = traktActivityRepository.getEpisodesWatchedSyncTimeStamp()
        val nitroDeferred = async { traktSyncDataSource.getUpNextNitro() }
        val hiddenDeferred = async { traktUserDataSource.getHiddenProgressWatched() }

        val nitroResponse = nitroDeferred.await()
        if (nitroResponse !is ApiResponse.Success) return@coroutineScope null
        val nitro = nitroResponse.body

        if (nitro.isEmpty() && !shouldSync(instant)) {
            logger.warning(
                LOG_TAG,
                "Nitro returned empty within recent sync window; skipping write to preserve local table.",
            )
            return@coroutineScope null
        }

        val hiddenResponse = hiddenDeferred.await()
        if (hiddenResponse !is ApiResponse.Success) return@coroutineScope null
        val hiddenIds = hiddenResponse.body
            .mapNotNull { it.show?.ids?.trakt }
            .toSet()

        nitro
            .filter { it.show.ids.trakt !in hiddenIds }
            // Load-bearing filter. Nitro can return null next_episode for reset shows
            // and other edge cases where the row should not surface in the watchlist.
            .filter { it.progress.nextEpisode != null }
            .map { it.toEntry() }
    }

    private fun shouldSync(instant: Instant?): Boolean {
        if (instant == null) return true
        val age = dateTimeProvider.now() - instant
        return age > CONTINUE_WATCHING_SYNC.duration
    }

    private companion object {
        const val LOG_TAG = "NitroContinueWatchingFetcher"
    }
}

private fun TraktUpNextNitroResponse.toEntry(): ContinueWatchingEntry {
    val lastWatchedAtMs = progress.lastWatchedAt
        ?.let { Instant.parse(it).toEpochMilliseconds() }
        ?: 0L
    return ContinueWatchingEntry(
        traktId = show.ids.trakt,
        tmdbId = show.ids.tmdb,
        airedEpisodes = progress.aired.toLong(),
        completedCount = progress.completed.toLong(),
        lastWatchedAt = lastWatchedAtMs,
        lastUpdatedAt = lastWatchedAtMs,
        title = show.title,
        year = show.year,
    )
}
