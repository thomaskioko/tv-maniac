package com.thomaskioko.tvmaniac.episodes.testing

import com.thomaskioko.tvmaniac.db.EpisodeById
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.episodes.api.model.MostWatchedShow
import com.thomaskioko.tvmaniac.episodes.api.model.RecentlyWatchedEpisode
import com.thomaskioko.tvmaniac.episodes.api.model.SeasonWatchProgress
import com.thomaskioko.tvmaniac.episodes.api.model.ShowMetadataSyncInfo
import com.thomaskioko.tvmaniac.episodes.api.model.ShowWatchProgress
import com.thomaskioko.tvmaniac.episodes.api.model.UpcomingEpisode
import com.thomaskioko.tvmaniac.episodes.api.model.WatchedEpisodeRuntime
import com.thomaskioko.tvmaniac.episodes.api.model.WatchedShowComposition
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
    val watchedAt: Long? = null,
    val useReleaseDate: Boolean = false,
)

public data class MarkSeasonWatchedCall(
    val showId: Long,
    val seasonNumber: Long,
    val markPreviousSeasons: Boolean,
    val watchedAt: Long? = null,
    val useReleaseDate: Boolean = false,
)

public data class MarkShowWatchedCall(
    val showId: Long,
    val watchedAt: Long? = null,
    val useReleaseDate: Boolean = false,
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

    private val watchedEpisodeRuntimesFlow = MutableStateFlow<List<WatchedEpisodeRuntime>>(emptyList())
    private val mostWatchedShowsFlow = MutableStateFlow<List<MostWatchedShow>>(emptyList())
    private val watchedShowCompositionFlow = MutableStateFlow<List<WatchedShowComposition>>(emptyList())

    public fun setWatchedEpisodeRuntimes(runtimes: List<WatchedEpisodeRuntime>) {
        watchedEpisodeRuntimesFlow.value = runtimes
    }

    public fun setWatchedShowComposition(composition: List<WatchedShowComposition>) {
        watchedShowCompositionFlow.value = composition
    }

    public fun setMostWatchedShows(shows: List<MostWatchedShow>) {
        mostWatchedShowsFlow.value = shows
    }
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

    public var lastMarkShowWatchedCall: MarkShowWatchedCall? = null
        private set

    public var lastMarkEpisodeUnwatchedCall: MarkEpisodeUnwatchedCall? = null
        private set

    public var lastUpcomingEpisodesLimit: Duration? = null
        private set

    private var syncUpcomingEpisodesBehavior: (suspend () -> Unit)? = null

    public fun setSyncUpcomingEpisodesBehavior(behavior: suspend () -> Unit) {
        syncUpcomingEpisodesBehavior = behavior
    }

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

    override fun observeWatchedEpisodeRuntimes(): Flow<List<WatchedEpisodeRuntime>> =
        watchedEpisodeRuntimesFlow.asStateFlow()

    override fun observeWatchedShowComposition(): Flow<List<WatchedShowComposition>> =
        watchedShowCompositionFlow.asStateFlow()

    override fun observeMostWatchedShows(limit: Long): Flow<List<MostWatchedShow>> =
        mostWatchedShowsFlow.asStateFlow()

    override suspend fun markEpisodeAsWatched(
        showId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        watchedAt: Long?,
        useReleaseDate: Boolean,
    ) {
        lastMarkEpisodeWatchedCall = MarkEpisodeWatchedCall(
            showId = showId,
            episodeId = episodeId,
            seasonNumber = seasonNumber,
            episodeNumber = episodeNumber,
            watchedAt = watchedAt,
            useReleaseDate = useReleaseDate,
        )
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

    override suspend fun markSeasonWatched(
        showId: Long,
        seasonNumber: Long,
        watchedAt: Long?,
        useReleaseDate: Boolean,
    ) {
        lastMarkSeasonWatchedCall = MarkSeasonWatchedCall(
            showId = showId,
            seasonNumber = seasonNumber,
            markPreviousSeasons = false,
            watchedAt = watchedAt,
            useReleaseDate = useReleaseDate,
        )
    }

    override suspend fun markEpisodeAndPreviousEpisodesWatched(
        showId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        watchedAt: Long?,
        useReleaseDate: Boolean,
    ) {
        lastMarkEpisodeWatchedCall = MarkEpisodeWatchedCall(
            showId = showId,
            episodeId = episodeId,
            seasonNumber = seasonNumber,
            episodeNumber = episodeNumber,
            markPreviousEpisodes = true,
            watchedAt = watchedAt,
            useReleaseDate = useReleaseDate,
        )
    }

    override suspend fun markSeasonAndPreviousSeasonsWatched(
        showId: Long,
        seasonNumber: Long,
        watchedAt: Long?,
        useReleaseDate: Boolean,
    ) {
        lastMarkSeasonWatchedCall = MarkSeasonWatchedCall(
            showId = showId,
            seasonNumber = seasonNumber,
            markPreviousSeasons = true,
            watchedAt = watchedAt,
            useReleaseDate = useReleaseDate,
        )
    }

    override suspend fun markShowWatched(showId: Long, watchedAt: Long?, useReleaseDate: Boolean) {
        lastMarkShowWatchedCall = MarkShowWatchedCall(
            showId = showId,
            watchedAt = watchedAt,
            useReleaseDate = useReleaseDate,
        )
    }

    override suspend fun markSeasonUnwatched(showId: Long, seasonNumber: Long) {}

    override fun observeUnwatchedCountInPreviousSeasons(
        showId: Long,
        seasonNumber: Long,
    ): Flow<Long> = unwatchedCountInPreviousSeasonsFlow.asStateFlow()

    override suspend fun getUpcomingEpisodesFromFollowedShows(limit: Duration): List<UpcomingEpisode> {
        lastUpcomingEpisodesLimit = limit
        return upcomingEpisodesFlow.value
    }

    override suspend fun syncUpcomingEpisodes(startDate: String, days: Int, forceRefresh: Boolean) {
        syncUpcomingEpisodesBehavior?.invoke()
    }

    override suspend fun getShowMetadataSyncInfo(showId: Long): ShowMetadataSyncInfo? =
        showMetadataSyncInfo[showId]
}
