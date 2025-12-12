package com.thomaskioko.tvmaniac.episodes.testing

import com.thomaskioko.tvmaniac.db.Watched_episodes
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.episodes.api.model.ContinueTrackingResult
import com.thomaskioko.tvmaniac.episodes.api.model.LastWatchedEpisode
import com.thomaskioko.tvmaniac.episodes.api.model.NextEpisodeWithShow
import com.thomaskioko.tvmaniac.episodes.api.model.SeasonWatchProgress
import com.thomaskioko.tvmaniac.episodes.api.model.ShowWatchProgress
import com.thomaskioko.tvmaniac.episodes.api.model.UnwatchedEpisode
import com.thomaskioko.tvmaniac.episodes.api.model.WatchProgress
import com.thomaskioko.tvmaniac.episodes.api.model.WatchProgressContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.time.Instant

data class MarkEpisodeWatchedCall(
    val showId: Long,
    val episodeId: Long,
    val seasonNumber: Long,
    val episodeNumber: Long,
    val markPreviousEpisodes: Boolean = false,
)

data class MarkSeasonWatchedCall(
    val showId: Long,
    val seasonNumber: Long,
    val markPreviousSeasons: Boolean,
)

data class MarkEpisodeUnwatchedCall(
    val showId: Long,
    val episodeId: Long,
)

class FakeEpisodeRepository : EpisodeRepository {
    private val nextEpisodesForWatchlist = MutableStateFlow<List<NextEpisodeWithShow>>(emptyList())
    private val watchProgressMap = mutableMapOf<Long, MutableStateFlow<WatchProgress>>()
    private val seasonWatchProgressFlow = MutableStateFlow(SeasonWatchProgress(0, 0, 0, 0))
    private val showWatchProgressFlow = MutableStateFlow(ShowWatchProgress(0, 0, 0))
    private val continueTrackingFlow = MutableStateFlow<ContinueTrackingResult?>(null)
    private val unwatchedCountBeforeFlow = MutableStateFlow(0)
    private val unwatchedCountInPreviousSeasonsFlow = MutableStateFlow(0L)

    var lastMarkEpisodeWatchedCall: MarkEpisodeWatchedCall? = null
        private set

    var lastMarkSeasonWatchedCall: MarkSeasonWatchedCall? = null
        private set

    var lastMarkEpisodeUnwatchedCall: MarkEpisodeUnwatchedCall? = null
        private set

    fun setNextEpisodesForWatchlist(episodes: List<NextEpisodeWithShow>) {
        nextEpisodesForWatchlist.value = episodes
    }


    fun setSeasonWatchProgress(progress: SeasonWatchProgress) {
        seasonWatchProgressFlow.value = progress
    }

    fun setShowWatchProgress(progress: ShowWatchProgress) {
        showWatchProgressFlow.value = progress
    }

    fun setContinueTrackingResult(result: ContinueTrackingResult?) {
        continueTrackingFlow.value = result
    }

    fun setUnwatchedCountInPreviousSeasons(count: Long) {
        unwatchedCountInPreviousSeasonsFlow.value = count
    }

    override fun observeNextEpisodesForWatchlist(): Flow<List<NextEpisodeWithShow>> =
        nextEpisodesForWatchlist.asStateFlow()

    override suspend fun markEpisodeAsWatched(
        showId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        watchedAt: Instant?,
    ) {
        lastMarkEpisodeWatchedCall = MarkEpisodeWatchedCall(showId, episodeId, seasonNumber, episodeNumber)
    }

    override suspend fun markEpisodeAsUnwatched(showId: Long, episodeId: Long) {
        lastMarkEpisodeUnwatchedCall = MarkEpisodeUnwatchedCall(showId, episodeId)
    }

    override fun observeWatchedEpisodes(showId: Long): Flow<List<Watched_episodes>> =
        MutableStateFlow(emptyList())

    override fun observeWatchProgress(showId: Long): Flow<WatchProgress> =
        watchProgressMap.getOrPut(showId) {
            MutableStateFlow(WatchProgress(showId, 0, null, null, null))
        }

    override suspend fun isEpisodeWatched(
        showId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
    ): Boolean = false

    override suspend fun clearCachedWatchHistoryForShow(showId: Long) {}

    override suspend fun getWatchProgressContext(showId: Long): WatchProgressContext =
        WatchProgressContext(
            showId = showId,
            totalEpisodes = 0,
            watchedEpisodes = 0,
            lastWatchedSeasonNumber = null,
            lastWatchedEpisodeNumber = null,
            isWatchingOutOfOrder = false,
            hasUnwatchedEarlierEpisodes = false,
            progressPercentage = 0f,
        )

    override suspend fun hasUnwatchedEarlierEpisodes(showId: Long): Boolean = false

    override fun observeLastWatchedEpisode(showId: Long): Flow<LastWatchedEpisode?> =
        MutableStateFlow(null)

    override fun observeSeasonWatchProgress(showId: Long, seasonNumber: Long): Flow<SeasonWatchProgress> =
        seasonWatchProgressFlow.asStateFlow()

    override fun observeShowWatchProgress(showId: Long): Flow<ShowWatchProgress> =
        showWatchProgressFlow.asStateFlow()

    override suspend fun markSeasonWatched(showId: Long, seasonNumber: Long, watchedAt: Instant?) {
        lastMarkSeasonWatchedCall = MarkSeasonWatchedCall(showId, seasonNumber, markPreviousSeasons = false)
    }

    override suspend fun markEpisodeAndPreviousEpisodesWatched(
        showId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        watchedAt: Instant?,
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
        watchedAt: Instant?,
    ) {
        lastMarkSeasonWatchedCall = MarkSeasonWatchedCall(showId, seasonNumber, markPreviousSeasons = true)
    }

    override suspend fun markSeasonUnwatched(showId: Long, seasonNumber: Long) {}

    override suspend fun getPreviousUnwatchedEpisodes(
        showId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
    ): List<UnwatchedEpisode> = List(unwatchedCountBeforeFlow.value) {
        UnwatchedEpisode(
            episodeId = it.toLong(),
            seasonNumber = seasonNumber,
            episodeNumber = it.toLong() + 1,
            seasonId = seasonNumber,
        )
    }

    override suspend fun getUnwatchedCountAfterFetchingPreviousSeasons(
        showId: Long,
        seasonNumber: Long,
    ): Long = unwatchedCountInPreviousSeasonsFlow.value

    override fun observeContinueTrackingEpisodes(showId: Long): Flow<ContinueTrackingResult?> =
        continueTrackingFlow.asStateFlow()

    override fun observeUnwatchedCountBefore(
        showId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
    ): Flow<Int> = unwatchedCountBeforeFlow.asStateFlow()

    override fun observeUnwatchedCountInPreviousSeasons(
        showId: Long,
        seasonNumber: Long,
    ): Flow<Long> = unwatchedCountInPreviousSeasonsFlow.asStateFlow()
}
