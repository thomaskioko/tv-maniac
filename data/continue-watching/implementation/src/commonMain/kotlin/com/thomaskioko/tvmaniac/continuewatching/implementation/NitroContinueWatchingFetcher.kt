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
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.time.Instant

@ContributesBinding(
    scope = AppScope::class,
    binding = binding<@Nitro ContinueWatchingFetcher>(),
)
@SingleIn(AppScope::class)
public class NitroContinueWatchingFetcher(
    private val traktSyncDataSource: TraktSyncRemoteDataSource,
    private val traktUserDataSource: TraktUserRemoteDataSource,
    private val traktActivityRepository: TraktActivityRepository,
    private val dateTimeProvider: DateTimeProvider,
    private val logger: Logger,
) : ContinueWatchingFetcher {

    public override suspend fun run(forceRefresh: Boolean): List<ContinueWatchingEntry>? = coroutineScope {
        val cursor = traktActivityRepository.getEpisodesWatchedSyncTimeStamp()
        val nitroDeferred = async { traktSyncDataSource.getUpNextNitro() }
        val hiddenDeferred = async { traktUserDataSource.getHiddenProgressWatched() }

        val nitroResponse = nitroDeferred.await()
        if (nitroResponse !is ApiResponse.Success) return@coroutineScope null
        val nitro = nitroResponse.body

        if (!forceRefresh && nitro.isEmpty() && cursorWithinGuardWindow(cursor)) {
            logger.warning(
                LOG_TAG,
                "Nitro returned empty within guard window; skipping write to preserve local table.",
            )
            return@coroutineScope null
        }

        val hiddenIds = hiddenDeferred.await().bodyOrEmpty()
            .mapNotNull { it.show?.ids?.trakt }
            .toSet()

        nitro
            .filter { it.show.ids.trakt !in hiddenIds }
            // Load-bearing filter. Same rationale as ProgressContinueWatchingFetcher: Nitro can
            // return null next_episode for reset shows and other edge cases where
            // the row should not surface in the watchlist.
            .filter { it.progress.nextEpisode != null }
            .map { it.toEntry() }
    }

    private fun cursorWithinGuardWindow(cursor: Instant?): Boolean {
        if (cursor == null) return false
        val age = dateTimeProvider.now() - cursor
        return age <= CONTINUE_WATCHING_SYNC.duration
    }

    private companion object {
        const val LOG_TAG = "NitroContinueWatchingFetcher"
    }
}

private fun <T> ApiResponse<List<T>>.bodyOrEmpty(): List<T> =
    (this as? ApiResponse.Success)?.body ?: emptyList()

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
    )
}
