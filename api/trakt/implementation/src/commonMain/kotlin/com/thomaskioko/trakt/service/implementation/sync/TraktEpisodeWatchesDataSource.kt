package com.thomaskioko.trakt.service.implementation.sync

import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.episodes.api.EpisodeWatchesDataSource
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeEntry
import com.thomaskioko.tvmaniac.episodes.api.WatchedShowBatch
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsDao
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.trakt.api.TraktEpisodeHistoryRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.TraktSyncRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowIds
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSyncItems
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSyncSeason
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSyncSeasonEpisode
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSyncShow
import com.thomaskioko.tvmaniac.trakt.api.model.TraktWatchedShowResponse
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.SingleIn
import kotlin.time.Instant

@SingleIn(AppScope::class)
@ContributesIntoSet(AppScope::class)
public class TraktEpisodeWatchesDataSource(
    private val remoteDataSource: TraktEpisodeHistoryRemoteDataSource,
    private val syncRemoteDataSource: TraktSyncRemoteDataSource,
    private val followedShowsDao: FollowedShowsDao,
) : EpisodeWatchesDataSource {

    override val provider: AccountProvider = AccountProvider.TRAKT

    override suspend fun getShowEpisodeWatches(showId: Long): List<WatchedEpisodeEntry> {
        val traktShowId = followedShowsDao.traktIdForTmdbId(showId) ?: return emptyList()
        return when (val response = remoteDataSource.getShowEpisodeWatches(traktShowId)) {
            is ApiResponse.Success -> {
                response.body.map { entry ->
                    WatchedEpisodeEntry(
                        showId = showId,
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

    override suspend fun addEpisodeEntries(entries: List<WatchedEpisodeEntry>) {
        if (entries.isEmpty()) return
        val shows = entries.toShowSyncItems()
        if (shows.isEmpty()) return

        when (remoteDataSource.addEpisodeWatches(TraktSyncItems(shows = shows))) {
            is ApiResponse.Success -> Unit
            is ApiResponse.Unauthenticated -> return
            is ApiResponse.Error -> throw Exception("Failed to add episodes to history")
        }
    }

    override suspend fun removeEpisodeEntries(entries: List<WatchedEpisodeEntry>) {
        if (entries.isEmpty()) return
        val shows = entries.toShowSyncItems()
        if (shows.isEmpty()) return

        when (remoteDataSource.removeEpisodeWatches(TraktSyncItems(shows = shows))) {
            is ApiResponse.Success -> Unit
            is ApiResponse.Unauthenticated -> return
            is ApiResponse.Error -> throw Exception("Failed to remove episodes from history")
        }
    }

    private fun List<WatchedEpisodeEntry>.toShowSyncItems(): List<TraktSyncShow> =
        groupBy { it.showId }.map { (traktId, showWatches) ->
            TraktSyncShow(
                ids = TraktShowIds(traktId = traktId),
                seasons = showWatches.groupBy { it.seasonNumber }.map { (seasonNumber, seasonWatches) ->
                    TraktSyncSeason(
                        number = seasonNumber,
                        episodes = seasonWatches.map { watch ->
                            TraktSyncSeasonEpisode(
                                number = watch.episodeNumber,
                                watchedAt = watch.watchedAt.toString(),
                            )
                        },
                    )
                },
            )
        }
}

internal class BulkWatchedShowsFetchException(message: String) : Exception(message)

private fun TraktWatchedShowResponse.toBatch(): WatchedShowBatch? {
    val tmdbId = show.ids.tmdb
    val seasons = seasons ?: return null
    val episodes = seasons.flatMap { season ->
        season.episodes.mapNotNull { episode ->
            val watchedAt = episode.lastWatchedAt?.let { runCatching { Instant.parse(it) }.getOrNull() }
                ?: return@mapNotNull null
            WatchedEpisodeEntry(
                showId = tmdbId ?: 0L,
                episodeId = null,
                seasonNumber = season.number,
                episodeNumber = episode.number,
                watchedAt = watchedAt,
                traktId = null,
                pendingAction = PendingAction.NOTHING,
            )
        }
    }
    return WatchedShowBatch(
        tmdbId = tmdbId,
        imdbId = show.ids.imdb,
        title = show.title,
        episodes = episodes,
    )
}
