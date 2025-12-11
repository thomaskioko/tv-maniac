package com.thomaskioko.tvmaniac.episodes.testing

import com.thomaskioko.tvmaniac.db.Watched_episodes
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.episodes.api.model.ContinueTrackingResult
import com.thomaskioko.tvmaniac.episodes.api.model.EpisodeWatchParams
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

    var lastMarkEpisodeWatchedCall: MarkEpisodeWatchedCall? = null
        private set

    var lastMarkSeasonWatchedCall: MarkSeasonWatchedCall? = null
        private set

    var lastMarkEpisodeUnwatchedCall: MarkEpisodeUnwatchedCall? = null
        private set

    private var unwatchedEpisodesBeforeResult: List<UnwatchedEpisode> = emptyList()
    private var unwatchedCountAfterFetchingPreviousSeasonsResult: Long = 0L
    private var earliestUnwatchedEpisodeResult: NextEpisodeWithShow? = null

    fun setUnwatchedEpisodesBefore(episodes: List<UnwatchedEpisode>) {
        unwatchedEpisodesBeforeResult = episodes
    }

    fun setUnwatchedCountAfterFetchingPreviousSeasons(count: Long) {
        unwatchedCountAfterFetchingPreviousSeasonsResult = count
    }

    fun setEarliestUnwatchedEpisode(episode: NextEpisodeWithShow?) {
        earliestUnwatchedEpisodeResult = episode
    }

    fun setNextEpisodesForWatchlist(episodes: List<NextEpisodeWithShow>) {
        nextEpisodesForWatchlist.value = episodes
    }

    fun setWatchProgress(showId: Long, progress: WatchProgress) {
        watchProgressMap.getOrPut(showId) {
            MutableStateFlow(WatchProgress(showId, 0, null, null, null))
        }.value = progress
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

    override fun observeNextEpisodesForWatchlist(): Flow<List<NextEpisodeWithShow>> =
        nextEpisodesForWatchlist.asStateFlow()

    override fun observeNextEpisodeForShow(showId: Long): Flow<NextEpisodeWithShow?> =
        MutableStateFlow(null)

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

    override suspend fun getLastWatchedEpisode(showId: Long): Watched_episodes? = null

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

    override suspend fun findEarliestUnwatchedEpisode(showId: Long): NextEpisodeWithShow? =
        earliestUnwatchedEpisodeResult

    override suspend fun isWatchingOutOfOrder(showId: Long): Boolean = false

    override fun observeLastWatchedEpisode(showId: Long): Flow<LastWatchedEpisode?> =
        MutableStateFlow(null)

    override fun observeSeasonWatchProgress(showId: Long, seasonNumber: Long): Flow<SeasonWatchProgress> =
        seasonWatchProgressFlow.asStateFlow()

    override fun observeShowWatchProgress(showId: Long): Flow<ShowWatchProgress> =
        showWatchProgressFlow.asStateFlow()

    override suspend fun markSeasonWatched(showId: Long, seasonNumber: Long, watchedAt: Instant?) {}

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

    override suspend fun getUnwatchedEpisodesBefore(
        showId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
    ): List<UnwatchedEpisode> = unwatchedEpisodesBeforeResult

    override suspend fun markMultipleEpisodesWatched(
        showId: Long,
        episodes: List<EpisodeWatchParams>,
        watchedAt: Instant?,
    ) {}

    override suspend fun getUnwatchedEpisodesInPreviousSeasons(
        showId: Long,
        seasonNumber: Long,
    ): List<UnwatchedEpisode> = emptyList()

    override suspend fun getUnwatchedEpisodeCountInPreviousSeasons(
        showId: Long,
        seasonNumber: Long,
    ): Long = 0L

    override suspend fun getUnwatchedCountAfterFetchingPreviousSeasons(
        showId: Long,
        seasonNumber: Long,
    ): Long = unwatchedCountAfterFetchingPreviousSeasonsResult

    override fun observeContinueTrackingEpisodes(showId: Long): Flow<ContinueTrackingResult?> =
        continueTrackingFlow.asStateFlow()
}
