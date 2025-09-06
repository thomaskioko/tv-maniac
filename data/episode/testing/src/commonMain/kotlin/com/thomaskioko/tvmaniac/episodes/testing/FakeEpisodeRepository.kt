package com.thomaskioko.tvmaniac.episodes.testing

import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Watched_episodes
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.episodes.api.model.NextEpisodeWithShow
import com.thomaskioko.tvmaniac.episodes.api.model.WatchProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus

class FakeEpisodeRepository : EpisodeRepository {
    private val nextEpisodesForWatchlist = MutableStateFlow<List<NextEpisodeWithShow>>(emptyList())
    private val nextEpisodeForShow = mutableMapOf<Long, MutableStateFlow<NextEpisodeWithShow?>>()
    private val watchedEpisodes = mutableMapOf<Long, MutableStateFlow<List<Watched_episodes>>>()
    private val watchProgress = mutableMapOf<Long, MutableStateFlow<WatchProgress>>()
    private val lastWatchedEpisode = mutableMapOf<Long, Watched_episodes?>()
    private val episodeWatchedStatus = mutableMapOf<String, Boolean>()

    // Use LocalDate for readable and deterministic testing timestamps
    private var currentDate = LocalDate(2024, 1, 1) // Jan 1, 2024

    fun setCurrentDate(date: LocalDate) {
        currentDate = date
    }

    private fun getNextTimestamp(): Long {
        // Get epoch seconds for current date at start of day in UTC
        val timestamp = currentDate.atStartOfDayIn(TimeZone.UTC).epochSeconds
        // Increment to next day for next episode using the proper API
        currentDate = currentDate.plus(DatePeriod(days = 1))
        return timestamp
    }

    fun setNextEpisodesForWatchlist(episodes: List<NextEpisodeWithShow>) {
        nextEpisodesForWatchlist.value = episodes
    }

    fun setNextEpisodeForShow(showId: Long, episode: NextEpisodeWithShow?) {
        nextEpisodeForShow.getOrPut(showId) { MutableStateFlow(episode) }.value = episode
    }

    fun setWatchedEpisodes(showId: Long, episodes: List<Watched_episodes>) {
        watchedEpisodes.getOrPut(showId) { MutableStateFlow(emptyList()) }.value = episodes
    }

    fun setWatchProgress(showId: Long, progress: WatchProgress) {
        watchProgress.getOrPut(showId) { MutableStateFlow(createDefaultWatchProgress(showId)) }.value = progress
    }

    fun setLastWatchedEpisode(showId: Long, episode: Watched_episodes?) {
        lastWatchedEpisode[showId] = episode
    }

    fun setEpisodeWatchedStatus(showId: Long, seasonNumber: Long, episodeNumber: Long, isWatched: Boolean) {
        episodeWatchedStatus["$showId:$seasonNumber:$episodeNumber"] = isWatched
    }

    override fun observeNextEpisodesForWatchlist(): Flow<List<NextEpisodeWithShow>> {
        return nextEpisodesForWatchlist
    }

    override fun observeNextEpisodeForShow(showId: Long): Flow<NextEpisodeWithShow?> {
        return nextEpisodeForShow.getOrPut(showId) { MutableStateFlow(null) }
    }

    override suspend fun markEpisodeAsWatched(
        showId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
    ) {
        val key = "$showId:$seasonNumber:$episodeNumber"
        episodeWatchedStatus[key] = true

        val currentList = watchedEpisodes[showId]?.value ?: emptyList()

        val newEpisode = Watched_episodes(
            id = currentList.size.toLong() + 1,
            show_id = Id(showId),
            episode_id = Id(episodeId),
            season_number = seasonNumber,
            episode_number = episodeNumber,
            watched_at = getNextTimestamp(), // Each episode gets next day timestamp
        )
        watchedEpisodes.getOrPut(showId) { MutableStateFlow(emptyList()) }.value = currentList + newEpisode
        lastWatchedEpisode[showId] = newEpisode
    }

    override suspend fun markEpisodeAsUnwatched(showId: Long, episodeId: Long) {
        val currentList = watchedEpisodes[showId]?.value ?: emptyList()
        watchedEpisodes[showId]?.value = currentList.filter { it.episode_id.id != episodeId }

        currentList.find { it.episode_id.id == episodeId }?.let { episode ->
            val key = "$showId:${episode.season_number}:${episode.episode_number}"
            episodeWatchedStatus[key] = false
        }
    }

    override fun observeWatchedEpisodes(showId: Long): Flow<List<Watched_episodes>> {
        return watchedEpisodes.getOrPut(showId) { MutableStateFlow(emptyList()) }
    }

    override fun observeWatchProgress(showId: Long): Flow<WatchProgress> {
        return watchProgress.getOrPut(showId) { MutableStateFlow(createDefaultWatchProgress(showId)) }
    }

    override suspend fun getLastWatchedEpisode(showId: Long): Watched_episodes? {
        return lastWatchedEpisode[showId]
    }

    override suspend fun isEpisodeWatched(
        showId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
    ): Boolean {
        val key = "$showId:$seasonNumber:$episodeNumber"
        return episodeWatchedStatus[key] ?: false
    }

    override suspend fun clearWatchHistoryForShow(showId: Long) {
        watchedEpisodes[showId]?.value = emptyList()
        lastWatchedEpisode.remove(showId)

        episodeWatchedStatus.keys.filter { it.startsWith("$showId:") }.forEach {
            episodeWatchedStatus.remove(it)
        }
    }

    private fun createDefaultWatchProgress(showId: Long): WatchProgress {
        return WatchProgress(
            showId = showId,
            totalEpisodesWatched = 0,
            lastSeasonWatched = null,
            lastEpisodeWatched = null,
            nextEpisode = null,
        )
    }
}
