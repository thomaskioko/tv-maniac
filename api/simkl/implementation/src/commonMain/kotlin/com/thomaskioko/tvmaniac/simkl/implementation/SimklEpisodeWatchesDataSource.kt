package com.thomaskioko.tvmaniac.simkl.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.episodes.api.EpisodeWatchesDataSource
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeEntry
import com.thomaskioko.tvmaniac.episodes.api.WatchedShowBatch
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.simkl.api.SimklSyncRemoteDataSource
import com.thomaskioko.tvmaniac.simkl.api.model.SimklHistoryEpisode
import com.thomaskioko.tvmaniac.simkl.api.model.SimklHistorySeason
import com.thomaskioko.tvmaniac.simkl.api.model.SimklHistoryShow
import com.thomaskioko.tvmaniac.simkl.api.model.SimklShowIds
import com.thomaskioko.tvmaniac.simkl.api.model.SimklSyncHistoryRequest
import com.thomaskioko.tvmaniac.simkl.api.model.SimklWatchedEpisode
import com.thomaskioko.tvmaniac.simkl.api.model.SimklWatchedSeason
import com.thomaskioko.tvmaniac.simkl.api.model.SimklWatchedShow
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.SingleIn
import kotlin.time.Instant

@SingleIn(AppScope::class)
@ContributesIntoSet(AppScope::class)
public class SimklEpisodeWatchesDataSource(
    private val syncRemoteDataSource: SimklSyncRemoteDataSource,
) : EpisodeWatchesDataSource {

    override val provider: SyncProviderSource = SyncProviderSource.SIMKL

    override suspend fun getAllWatchedShows(page: Int, limit: Int): List<WatchedShowBatch> {
        return when (val response = syncRemoteDataSource.getAllWatchedShows()) {
            is ApiResponse.Success -> {
                val batches = response.body.shows.map { it.toBatch() }
                if (page == 1) batches else emptyList()
            }
            is ApiResponse.Unauthenticated -> emptyList()
            is ApiResponse.Error -> throw BulkSimklWatchedShowsFetchException("Simkl bulk watched-shows fetch failed")
        }
    }

    override suspend fun getShowEpisodeWatches(showId: Long): List<WatchedEpisodeEntry> = emptyList()

    override suspend fun addEpisodeEntries(entries: List<WatchedEpisodeEntry>) {
        if (entries.isEmpty()) return
        val request = SimklSyncHistoryRequest(shows = entries.toSimklHistoryShows())
        when (syncRemoteDataSource.addWatchedHistory(request)) {
            is ApiResponse.Success -> Unit
            is ApiResponse.Unauthenticated -> return
            is ApiResponse.Error -> throw Exception("Simkl: failed to add episode history")
        }
    }

    override suspend fun removeEpisodeEntries(entries: List<WatchedEpisodeEntry>) {
        if (entries.isEmpty()) return
        val request = SimklSyncHistoryRequest(shows = entries.toSimklHistoryShows())
        when (syncRemoteDataSource.removeWatchedHistory(request)) {
            is ApiResponse.Success -> Unit
            is ApiResponse.Unauthenticated -> return
            is ApiResponse.Error -> throw Exception("Simkl: failed to remove episode history")
        }
    }

    private fun List<WatchedEpisodeEntry>.toSimklHistoryShows(): List<SimklHistoryShow> =
        groupBy { it.showId }.map { (showId, showWatches) ->
            SimklHistoryShow(
                ids = SimklShowIds(simkl = showId),
                seasons = showWatches.groupBy { it.seasonNumber }.map { (seasonNumber, seasonWatches) ->
                    SimklHistorySeason(
                        number = seasonNumber.toInt(),
                        episodes = seasonWatches.map { watch ->
                            SimklHistoryEpisode(number = watch.episodeNumber.toInt())
                        },
                    )
                },
            )
        }
}

internal class BulkSimklWatchedShowsFetchException(message: String) : Exception(message)

private fun SimklWatchedShow.toBatch(): WatchedShowBatch {
    val ids = show.ids
    val tmdbId = ids.tmdb?.toLongOrNull()
    val episodes = seasons.flatMap { season -> season.toEntries(tmdbId) }
    return WatchedShowBatch(
        tmdbId = tmdbId,
        imdbId = ids.imdb,
        title = show.title,
        providerShowId = ids.simkl?.toString(),
        episodes = episodes,
    )
}

private fun SimklWatchedSeason.toEntries(tmdbId: Long?): List<WatchedEpisodeEntry> =
    episodes.mapNotNull { episode -> episode.toEntry(seasonNumber = number, tmdbId = tmdbId) }

private fun SimklWatchedEpisode.toEntry(seasonNumber: Int, tmdbId: Long?): WatchedEpisodeEntry? {
    val parsedAt = watchedAt?.let { runCatching { Instant.parse(it) }.getOrNull() } ?: return null
    return WatchedEpisodeEntry(
        showId = tmdbId ?: 0L,
        episodeId = null,
        seasonNumber = seasonNumber.toLong(),
        episodeNumber = number.toLong(),
        watchedAt = parsedAt,
        traktId = null,
        pendingAction = PendingAction.NOTHING,
    )
}
