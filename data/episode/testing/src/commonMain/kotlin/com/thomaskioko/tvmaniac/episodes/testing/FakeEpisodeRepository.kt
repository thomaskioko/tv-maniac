package com.thomaskioko.tvmaniac.episodes.testing

import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.episodes.api.model.ContinueTrackingResult
import com.thomaskioko.tvmaniac.episodes.api.model.LastWatchedEpisode
import com.thomaskioko.tvmaniac.episodes.api.model.NextEpisodeWithShow
import com.thomaskioko.tvmaniac.episodes.api.model.SeasonWatchProgress
import com.thomaskioko.tvmaniac.episodes.api.model.ShowWatchProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.time.Instant

public data class MarkEpisodeWatchedCall(
    val showTraktId: Long,
    val episodeId: Long,
    val seasonNumber: Long,
    val episodeNumber: Long,
    val markPreviousEpisodes: Boolean = false,
)

public data class MarkSeasonWatchedCall(
    val showTraktId: Long,
    val seasonNumber: Long,
    val markPreviousSeasons: Boolean,
)

public data class MarkEpisodeUnwatchedCall(
    val showTraktId: Long,
    val episodeId: Long,
)

public class FakeEpisodeRepository : EpisodeRepository {
    private val nextEpisodesForWatchlist = MutableStateFlow<List<NextEpisodeWithShow>>(emptyList())
    private val seasonWatchProgressFlow = MutableStateFlow(SeasonWatchProgress(0, 0, 0, 0))
    private val showWatchProgressFlow = MutableStateFlow(ShowWatchProgress(0, 0, 0))
    private val allSeasonsWatchProgressFlow = MutableStateFlow<List<SeasonWatchProgress>>(emptyList())
    private val continueTrackingFlow = MutableStateFlow<ContinueTrackingResult?>(null)
    private val unwatchedCountInPreviousSeasonsFlow = MutableStateFlow(0L)

    public var lastMarkEpisodeWatchedCall: MarkEpisodeWatchedCall? = null
        private set

    public var lastMarkSeasonWatchedCall: MarkSeasonWatchedCall? = null
        private set

    public var lastMarkEpisodeUnwatchedCall: MarkEpisodeUnwatchedCall? = null
        private set

    public fun setNextEpisodesForWatchlist(episodes: List<NextEpisodeWithShow>) {
        nextEpisodesForWatchlist.value = episodes
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

    public fun setContinueTrackingResult(result: ContinueTrackingResult?) {
        continueTrackingFlow.value = result
    }

    public fun setUnwatchedCountInPreviousSeasons(count: Long) {
        unwatchedCountInPreviousSeasonsFlow.value = count
    }

    override fun observeNextEpisodesForWatchlist(): Flow<List<NextEpisodeWithShow>> =
        nextEpisodesForWatchlist.asStateFlow()

    override suspend fun markEpisodeAsWatched(
        showTraktId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        watchedAt: Instant?,
    ) {
        lastMarkEpisodeWatchedCall = MarkEpisodeWatchedCall(showTraktId, episodeId, seasonNumber, episodeNumber)
    }

    override suspend fun markEpisodeAsUnwatched(showTraktId: Long, episodeId: Long) {
        lastMarkEpisodeUnwatchedCall = MarkEpisodeUnwatchedCall(showTraktId, episodeId)
    }

    override fun observeLastWatchedEpisode(showTraktId: Long): Flow<LastWatchedEpisode?> =
        MutableStateFlow(null)

    override fun observeSeasonWatchProgress(showTraktId: Long, seasonNumber: Long): Flow<SeasonWatchProgress> =
        seasonWatchProgressFlow.asStateFlow()

    override fun observeShowWatchProgress(showTraktId: Long): Flow<ShowWatchProgress> =
        showWatchProgressFlow.asStateFlow()

    override fun observeAllSeasonsWatchProgress(showTraktId: Long): Flow<List<SeasonWatchProgress>> =
        allSeasonsWatchProgressFlow.asStateFlow()

    override suspend fun markSeasonWatched(showTraktId: Long, seasonNumber: Long, watchedAt: Instant?) {
        lastMarkSeasonWatchedCall = MarkSeasonWatchedCall(showTraktId, seasonNumber, markPreviousSeasons = false)
    }

    override suspend fun markEpisodeAndPreviousEpisodesWatched(
        showTraktId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        watchedAt: Instant?,
    ) {
        lastMarkEpisodeWatchedCall = MarkEpisodeWatchedCall(
            showTraktId,
            episodeId,
            seasonNumber,
            episodeNumber,
            markPreviousEpisodes = true,
        )
    }

    override suspend fun markSeasonAndPreviousSeasonsWatched(
        showTraktId: Long,
        seasonNumber: Long,
        watchedAt: Instant?,
    ) {
        lastMarkSeasonWatchedCall = MarkSeasonWatchedCall(showTraktId, seasonNumber, markPreviousSeasons = true)
    }

    override suspend fun markSeasonUnwatched(showTraktId: Long, seasonNumber: Long) {}

    override suspend fun getUnwatchedCountAfterFetchingPreviousSeasons(
        showTraktId: Long,
        seasonNumber: Long,
    ): Long = unwatchedCountInPreviousSeasonsFlow.value

    override fun observeContinueTrackingEpisodes(showTraktId: Long): Flow<ContinueTrackingResult?> =
        continueTrackingFlow.asStateFlow()

    override fun observeUnwatchedCountInPreviousSeasons(
        showTraktId: Long,
        seasonNumber: Long,
    ): Flow<Long> = unwatchedCountInPreviousSeasonsFlow.asStateFlow()
}
