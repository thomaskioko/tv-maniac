package com.thomaskioko.tvmaniac.domain.continuewatching

import com.thomaskioko.tvmaniac.domain.continuewatching.model.NextEpisodeInfo
import com.thomaskioko.tvmaniac.domain.continuewatching.model.WatchlistShowInfo
import com.thomaskioko.tvmaniac.upnext.api.model.NextEpisodeWithShow

internal fun NextEpisodeWithShow.toWatchlistShowInfo(): WatchlistShowInfo {
    val progress = if (totalCount > 0) watchedCount.toFloat() / totalCount else 0f
    return WatchlistShowInfo(
        showId = showId,
        tmdbId = showTmdbId,
        title = showName,
        posterImageUrl = showPoster,
        status = showStatus,
        year = showYear,
        seasonCount = seasonCount,
        episodeCount = episodeCount,
        episodesWatched = watchedCount,
        totalEpisodesTracked = totalCount,
        watchProgress = progress,
        lastWatchedAt = lastWatchedAt,
        nextEpisode = toNextEpisodeInfo(),
    )
}

internal fun NextEpisodeWithShow.toNextEpisodeInfo(): NextEpisodeInfo? {
    val resolvedEpisodeId = episodeId ?: return null
    val resolvedSeasonNumber = seasonNumber ?: return null
    val resolvedEpisodeNumber = episodeNumber ?: return null
    return NextEpisodeInfo(
        episodeId = resolvedEpisodeId,
        episodeTitle = episodeName ?: "",
        seasonNumber = resolvedSeasonNumber,
        episodeNumber = resolvedEpisodeNumber,
        stillPath = stillPath,
        firstAired = firstAired,
    )
}

internal fun WatchlistShowInfo.isCompleted(): Boolean =
    totalEpisodesTracked in 1..episodesWatched
