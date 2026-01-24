package com.thomaskioko.tvmaniac.episodes.implementation

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.episodes.api.EpisodeWatchesDataSource
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeEntry
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsDao
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.trakt.api.TraktEpisodeHistoryRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowIds
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSyncItems
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSyncSeason
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSyncSeasonEpisode
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSyncShow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.time.Instant

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class TraktEpisodeWatchesDataSource(
    private val remoteDataSource: TraktEpisodeHistoryRemoteDataSource,
    private val followedShowsDao: FollowedShowsDao,
) : EpisodeWatchesDataSource {

    override suspend fun getShowEpisodeWatches(showTraktId: Long): List<WatchedEpisodeEntry> {
        return when (val response = remoteDataSource.getShowEpisodeWatches(showTraktId)) {
            is ApiResponse.Success -> {
                response.body.mapNotNull { entry ->
                    val traktId = entry.show.ids.traktId ?: return@mapNotNull null
                    WatchedEpisodeEntry(
                        showTraktId = traktId,
                        episodeId = 0L,
                        seasonNumber = entry.episode.season.toLong(),
                        episodeNumber = entry.episode.number.toLong(),
                        watchedAt = Instant.parse(entry.watchedAt),
                        traktId = entry.id,
                        pendingAction = PendingAction.NOTHING,
                    )
                }
            }
            is ApiResponse.Error -> emptyList()
        }
    }

    override suspend fun addEpisodeWatches(watches: List<WatchedEpisodeEntry>) {
        if (watches.isEmpty()) return

        val showsMap = watches.groupBy { it.showTraktId }

        val shows = showsMap.mapNotNull { (showId, showWatches) ->
            val showEntry = followedShowsDao.entryWithTraktId(showId)
            val traktId = showEntry?.traktId ?: return@mapNotNull null

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
            is ApiResponse.Error -> throw Exception("Failed to add episodes to history")
        }
    }

    override suspend fun removeEpisodeWatches(traktHistoryIds: List<Long>) {
        if (traktHistoryIds.isEmpty()) return

        val items = TraktSyncItems(ids = traktHistoryIds)

        when (remoteDataSource.removeEpisodeWatches(items)) {
            is ApiResponse.Success -> Unit
            is ApiResponse.Error -> throw Exception("Failed to remove episodes from history")
        }
    }
}
