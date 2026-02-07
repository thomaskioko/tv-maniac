package com.thomaskioko.tvmaniac.upnext.testing

import com.thomaskioko.tvmaniac.upnext.api.UpNextDao
import com.thomaskioko.tvmaniac.upnext.api.model.NextEpisodeWithShow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

public class FakeUpNextDao : UpNextDao {
    private val nextEpisodesFlow = MutableStateFlow<List<NextEpisodeWithShow>>(emptyList())
    private val upsertedEpisodes = mutableMapOf<Long, UpNextEntry>()
    private val showProgressMap = mutableMapOf<Long, ShowProgress>()

    public fun setNextEpisodes(episodes: List<NextEpisodeWithShow>) {
        nextEpisodesFlow.value = episodes
    }

    public fun getUpsertedEpisodes(): Map<Long, UpNextEntry> = upsertedEpisodes.toMap()

    public fun getShowProgress(): Map<Long, ShowProgress> = showProgressMap.toMap()

    override fun observeNextEpisodesFromCache(): Flow<List<NextEpisodeWithShow>> =
        nextEpisodesFlow.asStateFlow()

    override suspend fun getNextEpisodesFromCache(): List<NextEpisodeWithShow> =
        nextEpisodesFlow.value

    override fun observeNextEpisodeForShow(showTraktId: Long): Flow<List<NextEpisodeWithShow>> =
        nextEpisodesFlow.map { episodes -> episodes.filter { it.showTraktId == showTraktId } }

    override suspend fun upsert(
        showTraktId: Long,
        episodeTraktId: Long?,
        seasonNumber: Long?,
        episodeNumber: Long?,
        title: String?,
        overview: String?,
        runtime: Long?,
        firstAired: Long?,
        imageUrl: String?,
        isShowComplete: Boolean,
        lastEpisodeSeason: Long?,
        lastEpisodeNumber: Long?,
        traktLastWatchedAt: Long?,
        updatedAt: Long,
    ) {
        upsertedEpisodes[showTraktId] = UpNextEntry(
            showTraktId = showTraktId,
            episodeTraktId = episodeTraktId,
            seasonNumber = seasonNumber,
            episodeNumber = episodeNumber,
            title = title,
            overview = overview,
            runtime = runtime,
            firstAired = firstAired,
            imageUrl = imageUrl,
            isShowComplete = isShowComplete,
            lastEpisodeSeason = lastEpisodeSeason,
            lastEpisodeNumber = lastEpisodeNumber,
            traktLastWatchedAt = traktLastWatchedAt,
        )
    }

    override suspend fun upsertShowProgress(
        showTraktId: Long,
        watchedCount: Long,
        totalCount: Long,
    ) {
        showProgressMap[showTraktId] = ShowProgress(
            showTraktId = showTraktId,
            watchedCount = watchedCount,
            totalCount = totalCount,
        )
    }

    override suspend fun advanceAfterWatched(
        showTraktId: Long,
        watchedSeason: Long,
        watchedEpisode: Long,
    ) {
        upsertedEpisodes[showTraktId]?.let { entry ->
            if (entry.lastEpisodeSeason == watchedSeason && entry.lastEpisodeNumber == watchedEpisode) {
                upsertedEpisodes[showTraktId] = entry.copy(isShowComplete = true)
            } else {
                upsertedEpisodes[showTraktId] = entry.copy(
                    episodeNumber = (entry.episodeNumber ?: 0) + 1,
                    episodeTraktId = 0,
                    title = null,
                    overview = null,
                    runtime = null,
                    firstAired = null,
                    imageUrl = null,
                )
            }
        }
    }

    override suspend fun deleteForShow(showTraktId: Long) {
        upsertedEpisodes.remove(showTraktId)
        showProgressMap.remove(showTraktId)
    }

    override suspend fun deleteAll() {
        upsertedEpisodes.clear()
        showProgressMap.clear()
        nextEpisodesFlow.value = emptyList()
    }
}

public data class UpNextEntry(
    val showTraktId: Long,
    val episodeTraktId: Long?,
    val seasonNumber: Long?,
    val episodeNumber: Long?,
    val title: String?,
    val overview: String?,
    val runtime: Long?,
    val firstAired: Long?,
    val imageUrl: String?,
    val isShowComplete: Boolean,
    val lastEpisodeSeason: Long?,
    val lastEpisodeNumber: Long?,
    val traktLastWatchedAt: Long?,
)

public data class ShowProgress(
    val showTraktId: Long,
    val watchedCount: Long,
    val totalCount: Long,
)
