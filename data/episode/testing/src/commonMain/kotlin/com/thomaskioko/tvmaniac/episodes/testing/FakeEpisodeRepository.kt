package com.thomaskioko.tvmaniac.episodes.testing

import com.thomaskioko.tvmaniac.db.EpisodeById
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.episodes.api.model.RecentlyWatchedEpisode
import com.thomaskioko.tvmaniac.episodes.api.model.SeasonWatchProgress
import com.thomaskioko.tvmaniac.episodes.api.model.ShowMetadataSyncInfo
import com.thomaskioko.tvmaniac.episodes.api.model.ShowWatchProgress
import com.thomaskioko.tvmaniac.episodes.api.model.UpcomingEpisode
import com.thomaskioko.tvmaniac.upnext.api.model.NextEpisodeWithShow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.time.Duration

public data class MarkEpisodeWatchedCall(
    val showId: Long,
    val episodeId: Long,
    val seasonNumber: Long,
    val episodeNumber: Long,
    val markPreviousEpisodes: Boolean = false,
)

public data class MarkSeasonWatchedCall(
    val showId: Long,
    val seasonNumber: Long,
    val markPreviousSeasons: Boolean,
)

public data class MarkEpisodeUnwatchedCall(
    val showId: Long,
    val episodeId: Long,
)

public data class SyncParams(
    val startDate: String,
    val days: Int,
    val forceRefresh: Boolean,
)

public class FakeEpisodeRepository : EpisodeRepository {
    private val nextEpisodesForWatchlist = MutableStateFlow<List<NextEpisodeWithShow>>(emptyList())
    private val episodeByIdFlow = MutableStateFlow<EpisodeById?>(null)
    private val seasonWatchProgressFlow = MutableStateFlow(SeasonWatchProgress(0, 0, 0, 0))
    private val showWatchProgressFlow = MutableStateFlow(ShowWatchProgress(0, 0, 0))
    private val allSeasonsWatchProgressFlow = MutableStateFlow<List<SeasonWatchProgress>>(emptyList())
    private val unwatchedCountInPreviousSeasonsFlow = MutableStateFlow(0L)
    private val upcomingEpisodesFlow = MutableStateFlow<List<UpcomingEpisode>>(emptyList())
    private val recentlyWatchedFlow = MutableStateFlow<List<RecentlyWatchedEpisode>>(emptyList())
    private val showMetadataSyncInfo = mutableMapOf<Long, ShowMetadataSyncInfo?>()

    public var lastMarkEpisodeWatchedCall: MarkEpisodeWatchedCall? = null
        private set

    public var lastMarkSeasonWatchedCall: MarkSeasonWatchedCall? = null
        private set

    public var lastMarkEpisodeUnwatchedCall: MarkEpisodeUnwatchedCall? = null
        private set

    public fun setEpisodeById(episode: EpisodeById?) {
        episodeByIdFlow.value = episode
    }

    public fun setSeasonWatchProgress(progress: SeasonWatchProgress) {
        seasonWatchProgressFlow.value = progress
    }

    public fun setShowWatchProgress(progress: ShowWatchProgress) {
        showWatchProgressFlow.value = progress
    }

    public fun setAllSeasonsWatchProgress(progressList: List<SeasonWatchProgress>) {
        allSeasonsWatchProgressFlow.value = progressList
    }

    public fun setUnwatchedCountInPreviousSeasons(count: Long) {
        unwatchedCountInPreviousSeasonsFlow.value = count
    }

    public fun setUpcomingEpisodes(episodes: List<UpcomingEpisode>) {
        upcomingEpisodesFlow.value = episodes
    }

    public fun setRecentlyWatched(episodes: List<RecentlyWatchedEpisode>) {
        recentlyWatchedFlow.value = episodes
    }

    public fun setShowMetadataSyncInfo(showId: Long, info: ShowMetadataSyncInfo?) {
        showMetadataSyncInfo[showId] = info
    }

    override fun observeEpisodeById(episodeId: Long): Flow<EpisodeById?> =
        episodeByIdFlow.asStateFlow()

    override fun observeRecentlyWatched(limit: Long): Flow<List<RecentlyWatchedEpisode>> =
        recentlyWatchedFlow.asStateFlow()

    override suspend fun markEpisodeAsWatched(
        showId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
    ) {
        lastMarkEpisodeWatchedCall = MarkEpisodeWatchedCall(showId, episodeId, seasonNumber, episodeNumber)
    }

    override suspend fun markEpisodeAsUnwatched(showId: Long, episodeId: Long) {
        lastMarkEpisodeUnwatchedCall = MarkEpisodeUnwatchedCall(showId, episodeId)
    }

    override fun observeSeasonWatchProgress(showId: Long, seasonNumber: Long): Flow<SeasonWatchProgress> =
        seasonWatchProgressFlow.asStateFlow()

    override fun observeShowWatchProgress(showId: Long): Flow<ShowWatchProgress> =
        showWatchProgressFlow.asStateFlow()

    override fun observeAllSeasonsWatchProgress(showId: Long): Flow<List<SeasonWatchProgress>> =
        allSeasonsWatchProgressFlow.asStateFlow()

    override suspend fun markSeasonWatched(showId: Long, seasonNumber: Long) {
        lastMarkSeasonWatchedCall = MarkSeasonWatchedCall(showId, seasonNumber, markPreviousSeasons = false)
    }

    override suspend fun markEpisodeAndPreviousEpisodesWatched(
        showId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
    ) {
        lastMarkEpisodeWatchedCall = MarkEpisodeWatchedCall(
            showId,
            episodeId,
            seasonNumber,
            episodeNumber,
            markPreviousEpisodes = true,
        )
    }

    override suspend fun markSeasonAndPreviousSeasonsWatched(
        showId: Long,
        seasonNumber: Long,
    ) {
        lastMarkSeasonWatchedCall = MarkSeasonWatchedCall(showId, seasonNumber, markPreviousSeasons = true)
    }

    override suspend fun markSeasonUnwatched(showId: Long, seasonNumber: Long) {}

    override fun observeUnwatchedCountInPreviousSeasons(
        showId: Long,
        seasonNumber: Long,
    ): Flow<Long> = unwatchedCountInPreviousSeasonsFlow.asStateFlow()

    override suspend fun getUpcomingEpisodesFromFollowedShows(limit: Duration): List<UpcomingEpisode> =
        upcomingEpisodesFlow.value

    override suspend fun syncUpcomingEpisodes(startDate: String, days: Int, forceRefresh: Boolean) {
    }

    override suspend fun getShowMetadataSyncInfo(showId: Long): ShowMetadataSyncInfo? =
        showMetadataSyncInfo[showId]
}
