package com.thomaskioko.tvmaniac.episodes.implementation

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.episodes.api.EpisodeWatchesDataSource
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeEntry
import com.thomaskioko.tvmaniac.episodes.api.WatchedShowBatch
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsDao
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.trakt.api.TraktEpisodeHistoryRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.TraktSyncRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktEpisodeIds
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowIds
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSyncEpisode
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSyncItems
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSyncSeason
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSyncSeasonEpisode
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSyncShow
import com.thomaskioko.tvmaniac.trakt.api.model.TraktWatchedShowResponse
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlin.time.Instant

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class TraktEpisodeWatchesDataSource(
    private val remoteDataSource: TraktEpisodeHistoryRemoteDataSource,
    private val syncRemoteDataSource: TraktSyncRemoteDataSource,
    private val followedShowsDao: FollowedShowsDao,
) : EpisodeWatchesDataSource {

    override suspend fun getShowEpisodeWatches(showId: Long): List<WatchedEpisodeEntry> {
        return when (val response = remoteDataSource.getShowEpisodeWatches(showId)) {
            is ApiResponse.Success -> {
                response.body.mapNotNull { entry ->
                    val traktId = entry.show.ids.traktId ?: return@mapNotNull null
                    WatchedEpisodeEntry(
                        showId = traktId,
                        episodeId = 0L,
                        seasonNumber = entry.episode.season.toLong(),
                        episodeNumber = entry.episode.number.toLong(),
                        watchedAt = Instant.parse(entry.watchedAt),
                        traktId = entry.id,
                        pendingAction = PendingAction.NOTHING,
                    )
                }
            }
            is ApiResponse.Unauthenticated -> emptyList()
            is ApiResponse.Error -> emptyList()
        }
    }

    override suspend fun getAllWatchedShows(page: Int, limit: Int): List<WatchedShowBatch> {
        val response = syncRemoteDataSource.getWatchedShows(
            page = page,
            limit = limit,
            extended = "full",
        )
        return when (response) {
            is ApiResponse.Success -> response.body.mapNotNull { it.toBatch() }
            is ApiResponse.Unauthenticated -> emptyList()
            is ApiResponse.Error -> throw BulkWatchedShowsFetchException(
                "Bulk watched-shows fetch failed for page=$page",
            )
        }
    }

    override suspend fun addEpisodeWatches(watches: List<WatchedEpisodeEntry>) {
        if (watches.isEmpty()) return

        val showsMap = watches.groupBy { it.showId }

        val shows = showsMap.mapNotNull { (showId, showWatches) ->
            val showEntry = followedShowsDao.entryWithTraktId(showId)
            val traktId = showEntry?.showId ?: return@mapNotNull null

            val seasons = showWatches
                .groupBy { it.seasonNumber }
                .map { (seasonNumber, seasonWatches) ->
                    TraktSyncSeason(
                        number = seasonNumber,
                        episodes = seasonWatches.map { watch ->
                            TraktSyncSeasonEpisode(
                                number = watch.episodeNumber,
                                watchedAt = watch.watchedAt.toString(),
                            )
                        },
                    )
                }

            TraktSyncShow(
                ids = TraktShowIds(traktId = traktId),
                seasons = seasons,
            )
        }

        if (shows.isEmpty()) return

        val items = TraktSyncItems(shows = shows)

        when (remoteDataSource.addEpisodeWatches(items)) {
            is ApiResponse.Success -> Unit
            is ApiResponse.Unauthenticated -> return
            is ApiResponse.Error -> throw Exception("Failed to add episodes to history")
        }
    }

    override suspend fun removeEpisodeWatches(episodeTraktIds: List<Long>) {
        if (episodeTraktIds.isEmpty()) return

        val items = TraktSyncItems(
            episodes = episodeTraktIds.map { id ->
                TraktSyncEpisode(ids = TraktEpisodeIds(traktId = id))
            },
        )

        when (remoteDataSource.removeEpisodeWatches(items)) {
            is ApiResponse.Success -> Unit
            is ApiResponse.Unauthenticated -> return
            is ApiResponse.Error -> throw Exception("Failed to remove episodes from history")
        }
    }
}

internal class BulkWatchedShowsFetchException(message: String) : Exception(message)

private fun TraktWatchedShowResponse.toBatch(): WatchedShowBatch? {
    val showId = show.ids.trakt
    val seasons = seasons ?: return null
    val episodes = seasons.flatMap { season ->
        season.episodes.mapNotNull { episode ->
            val watchedAt = episode.lastWatchedAt?.let { runCatching { Instant.parse(it) }.getOrNull() }
                ?: return@mapNotNull null
            WatchedEpisodeEntry(
                showId = showId,
                episodeId = null,
                seasonNumber = season.number,
                episodeNumber = episode.number,
                watchedAt = watchedAt,
                traktId = null,
                pendingAction = PendingAction.NOTHING,
            )
        }
    }
    return WatchedShowBatch(showId = showId, episodes = episodes)
}
