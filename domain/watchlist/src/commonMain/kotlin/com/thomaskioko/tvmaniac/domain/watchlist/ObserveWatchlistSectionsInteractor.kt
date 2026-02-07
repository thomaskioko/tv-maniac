package com.thomaskioko.tvmaniac.domain.watchlist

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.domain.watchlist.model.NextEpisodeInfo
import com.thomaskioko.tvmaniac.domain.watchlist.model.WatchlistSections
import com.thomaskioko.tvmaniac.domain.watchlist.model.WatchlistShowInfo
import com.thomaskioko.tvmaniac.upnext.api.UpNextRepository
import com.thomaskioko.tvmaniac.upnext.api.model.NextEpisodeWithShow
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject

private const val THREE_WEEKS_MILLIS = 21 * 24 * 60 * 60 * 1000L

@Inject
public class ObserveWatchlistSectionsInteractor(
    private val upNextRepository: UpNextRepository,
    private val dateTimeProvider: DateTimeProvider,
) : SubjectInteractor<String, WatchlistSections>() {

    override fun createObservable(params: String): Flow<WatchlistSections> {
        return upNextRepository.observeNextEpisodesForWatchlist()
            .map { episodes ->
                episodes
                    .filter { params.isBlank() || it.showName.contains(params, ignoreCase = true) }
                    .map { it.toWatchlistShowInfo() }
                    .filterNot { it.isCompleted() }
                    .groupBySections(dateTimeProvider.nowMillis())
            }
    }
}

private fun WatchlistShowInfo.isCompleted(): Boolean {
    return totalEpisodesTracked in 1..episodesWatched
}

private fun NextEpisodeWithShow.toWatchlistShowInfo(): WatchlistShowInfo {
    val progress = if (totalCount > 0) watchedCount.toFloat() / totalCount else 0f
    return WatchlistShowInfo(
        traktId = showTraktId,
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
        followedAt = followedAt,
        nextEpisode = toNextEpisodeInfo(),
    )
}

private fun NextEpisodeWithShow.toNextEpisodeInfo(): NextEpisodeInfo {
    return NextEpisodeInfo(
        episodeId = episodeId,
        episodeTitle = episodeName ?: "",
        seasonNumber = seasonNumber,
        episodeNumber = episodeNumber,
        stillPath = stillPath,
        firstAired = firstAired,
    )
}

private fun List<WatchlistShowInfo>.groupBySections(currentTimeMillis: Long): WatchlistSections {
    val threeWeeksAgo = currentTimeMillis - THREE_WEEKS_MILLIS

    val watchNext = mutableListOf<WatchlistShowInfo>()
    val stale = mutableListOf<WatchlistShowInfo>()

    forEach { item ->
        val lastWatched = item.lastWatchedAt ?: 0L
        val followedAt = item.followedAt ?: 0L

        val isStale = when {
            lastWatched > 0 -> lastWatched < threeWeeksAgo
            followedAt > 0 -> followedAt < threeWeeksAgo
            else -> false
        }

        if (isStale) stale.add(item) else watchNext.add(item)
    }

    return WatchlistSections(
        watchNext = watchNext,
        stale = stale,
    )
}
