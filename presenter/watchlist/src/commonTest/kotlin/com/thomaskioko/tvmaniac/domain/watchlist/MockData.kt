package com.thomaskioko.tvmaniac.domain.watchlist

import com.thomaskioko.tvmaniac.episodes.api.model.NextEpisodeWithShow
import com.thomaskioko.tvmaniac.watchlist.presenter.model.NextEpisodeItem
import com.thomaskioko.tvmaniac.watchlist.presenter.model.WatchlistItem
import kotlinx.collections.immutable.toPersistentList

val cachedNextEpisodes = mutableListOf(
    NextEpisodeWithShow(
        showTraktId = 84958,
        showTmdbId = 84958,
        showName = "Loki",
        showPoster = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        showStatus = "Ended",
        showYear = "2024",
        episodeId = 1L,
        episodeName = "Episode 1",
        seasonId = 1L,
        seasonNumber = 1L,
        episodeNumber = 1L,
        runtime = 45L,
        stillPath = "/still.jpg",
        overview = "Overview",
        firstAired = null,
        lastWatchedAt = null,
        seasonCount = 0,
        episodeCount = 0,
        watchedCount = 0,
        totalCount = 0,
    ),
)

val updatedNextEpisodes = listOf(
    NextEpisodeWithShow(
        showTraktId = 84958,
        showTmdbId = 84958,
        showName = "Loki",
        showPoster = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        showStatus = "Ended",
        showYear = "2024",
        episodeId = 1L,
        episodeName = "Episode 1",
        seasonId = 1L,
        seasonNumber = 1L,
        episodeNumber = 1L,
        runtime = 45L,
        stillPath = "/still.jpg",
        overview = "Overview",
        firstAired = null,
        lastWatchedAt = null,
        seasonCount = 0,
        episodeCount = 0,
        watchedCount = 0,
        totalCount = 0,
    ),
    NextEpisodeWithShow(
        showTraktId = 1232,
        showTmdbId = 1232,
        showName = "The Lazarus Project",
        showPoster = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        showStatus = "Ended",
        showYear = "2024",
        episodeId = 2L,
        episodeName = "Episode 1",
        seasonId = 2L,
        seasonNumber = 1L,
        episodeNumber = 1L,
        runtime = 45L,
        stillPath = "/still.jpg",
        overview = "Overview",
        firstAired = null,
        lastWatchedAt = null,
        seasonCount = 0,
        episodeCount = 0,
        watchedCount = 0,
        totalCount = 0,
    ),
)

internal fun expectedUiResult(
    episodes: List<NextEpisodeWithShow> = updatedNextEpisodes,
) = episodes
    .map {
        val progress = if (it.totalCount > 0) it.watchedCount.toFloat() / it.totalCount else 0f
        WatchlistItem(
            traktId = it.showTraktId,
            title = it.showName,
            posterImageUrl = it.showPoster,
            status = it.showStatus,
            year = it.showYear,
            seasonCount = it.seasonCount,
            episodeCount = it.episodeCount,
            episodesWatched = it.watchedCount,
            totalEpisodesTracked = it.totalCount,
            watchProgress = progress,
            lastWatchedAt = it.lastWatchedAt,
            nextEpisode = NextEpisodeItem(
                episodeId = it.episodeId,
                episodeTitle = it.episodeName ?: "",
                episodeNumberFormatted = "S${it.seasonNumber.toString().padStart(2, '0')} | E${it.episodeNumber.toString().padStart(2, '0')}",
                seasonNumber = it.seasonNumber,
                episodeNumber = it.episodeNumber,
                stillPath = it.stillPath,
                firstAired = it.firstAired,
            ),
        )
    }
    .toPersistentList()
