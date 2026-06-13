package com.thomaskioko.tvmaniac.episodes.testing

import com.thomaskioko.tvmaniac.db.GetEntriesByPendingAction
import com.thomaskioko.tvmaniac.db.GetWatchedEpisodes
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeDao
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeEntry
import com.thomaskioko.tvmaniac.episodes.api.model.EpisodeWatchParams
import com.thomaskioko.tvmaniac.episodes.api.model.RecentlyWatchedEpisode
import com.thomaskioko.tvmaniac.episodes.api.model.SeasonWatchProgress
import com.thomaskioko.tvmaniac.episodes.api.model.ShowWatchProgress
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

public class FakeWatchedEpisodeDao(
    private val pendingActionsCount: Long = 0L,
) : WatchedEpisodeDao {

    private val watchedFlow = MutableStateFlow<List<GetWatchedEpisodes>>(emptyList())
    private val recentFlow = MutableStateFlow<List<RecentlyWatchedEpisode>>(emptyList())

    public fun setWatchedEpisodes(entries: List<GetWatchedEpisodes>): Unit {
        watchedFlow.value = entries
    }

    override fun observeWatchedEpisodes(showId: Long): Flow<List<GetWatchedEpisodes>> =
        watchedFlow.map { list -> list.filter { it.show_id.id == showId } }

    override fun observeRecentlyWatched(limit: Long): Flow<List<RecentlyWatchedEpisode>> =
        recentFlow.asStateFlow()

    override fun observeSeasonWatchProgress(showId: Long, seasonNumber: Long): Flow<SeasonWatchProgress> =
        MutableStateFlow(SeasonWatchProgress(showId = showId, seasonNumber = seasonNumber, watchedCount = 0, totalCount = 0))

    override fun observeShowWatchProgress(showId: Long): Flow<ShowWatchProgress> =
        MutableStateFlow(ShowWatchProgress(showId = showId, watchedCount = 0, totalCount = 0))

    override fun observeAllSeasonsWatchProgress(showId: Long): Flow<List<SeasonWatchProgress>> =
        MutableStateFlow(emptyList())

    override suspend fun markAsWatched(
        showId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        includeSpecials: Boolean,
    ): Unit = Unit

    override suspend fun markAsUnwatched(showId: Long, episodeId: Long, includeSpecials: Boolean): Unit = Unit

    override suspend fun markSeasonAsWatched(
        showId: Long,
        seasonNumber: Long,
        episodes: List<EpisodeWatchParams>,
        includeSpecials: Boolean,
    ): Unit = Unit

    override suspend fun markSeasonAsUnwatched(showId: Long, seasonNumber: Long, includeSpecials: Boolean): Unit = Unit

    override suspend fun markSeasonAndPreviousAsWatched(showId: Long, seasonNumber: Long, includeSpecials: Boolean): Unit = Unit

    override suspend fun markEpisodeAndPreviousAsWatched(
        showId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        includeSpecials: Boolean,
    ): Unit = Unit

    override suspend fun getEpisodesForSeason(showId: Long, seasonNumber: Long): List<EpisodeWatchParams> = emptyList()

    override suspend fun getUnwatchedEpisodeCountInPreviousSeasons(
        showId: Long,
        seasonNumber: Long,
        includeSpecials: Boolean,
    ): Long = 0L

    override fun observeUnwatchedCountInPreviousSeasons(
        showId: Long,
        seasonNumber: Long,
        includeSpecials: Boolean,
    ): Flow<Long> = MutableStateFlow(0L)

    override suspend fun entriesByPendingAction(action: PendingAction): List<GetEntriesByPendingAction> = emptyList()

    override suspend fun updatePendingAction(id: Long, action: PendingAction): Unit = Unit

    override fun deleteAll(): Unit = Unit

    override suspend fun countPendingActions(): Long = pendingActionsCount

    override suspend fun deleteById(id: Long): Unit = Unit

    override suspend fun upsertBatchFromTrakt(
        showId: Long,
        entries: List<WatchedEpisodeEntry>,
        includeSpecials: Boolean,
    ): Unit = Unit
}
