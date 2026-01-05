package com.thomaskioko.tvmaniac.domain.watchlist

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.db.FollowedShows
import com.thomaskioko.tvmaniac.db.SearchFollowedShows
import com.thomaskioko.tvmaniac.domain.watchlist.model.NextEpisodeInfo
import com.thomaskioko.tvmaniac.domain.watchlist.model.WatchlistSections
import com.thomaskioko.tvmaniac.domain.watchlist.model.WatchlistShowInfo
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.episodes.api.model.NextEpisodeWithShow
import com.thomaskioko.tvmaniac.shows.api.WatchlistRepository
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject
import kotlin.jvm.JvmName

private const val THREE_WEEKS_MILLIS = 21 * 24 * 60 * 60 * 1000L

@Inject
public class ObserveWatchlistSectionsInteractor(
    private val watchlistRepository: WatchlistRepository,
    private val episodeRepository: EpisodeRepository,
    private val dateTimeProvider: DateTimeProvider,
) : SubjectInteractor<String, WatchlistSections>() {

    override fun createObservable(params: String): Flow<WatchlistSections> {
        return episodeRepository.observeNextEpisodesForWatchlist()
            .flatMapLatest { episodes ->
                val nextEpisodeMap = episodes.associateBy { it.showId }

                if (params.isNotBlank()) {
                    watchlistRepository.searchWatchlistByQuery(params).map { list ->
                        list.toWatchlistSections(nextEpisodeMap, dateTimeProvider.nowMillis())
                    }
                } else {
                    watchlistRepository.observeWatchlist().map { list ->
                        list.toWatchlistSections(nextEpisodeMap, dateTimeProvider.nowMillis())
                    }
                }
            }
    }
}

private fun List<FollowedShows>.toWatchlistSections(
    nextEpisodeMap: Map<Long, NextEpisodeWithShow>,
    currentTimeMillis: Long,
): WatchlistSections {
    val items = map { it.toWatchlistShowInfo(nextEpisodeMap) }
    return items.groupBySections(currentTimeMillis)
}

@JvmName("searchToWatchlistSections")
private fun List<SearchFollowedShows>.toWatchlistSections(
    nextEpisodeMap: Map<Long, NextEpisodeWithShow>,
    currentTimeMillis: Long,
): WatchlistSections {
    val items = map { it.toWatchlistShowInfo(nextEpisodeMap) }
    return items.groupBySections(currentTimeMillis)
}

private fun FollowedShows.toWatchlistShowInfo(nextEpisodeMap: Map<Long, NextEpisodeWithShow>): WatchlistShowInfo {
    val watched = watched_count
    val total = total_episode_count
    val progress = if (total > 0) watched.toFloat() / total else 0f
    val nextEp = nextEpisodeMap[id.id]
    return WatchlistShowInfo(
        tmdbId = id.id,
        title = name,
        posterImageUrl = poster_path,
        status = status,
        year = first_air_date,
        seasonCount = season_count ?: 0,
        episodeCount = episode_count ?: 0,
        episodesWatched = watched,
        totalEpisodesTracked = total,
        watchProgress = progress,
        lastWatchedAt = nextEp?.lastWatchedAt,
        nextEpisode = nextEp?.toNextEpisodeInfo(),
    )
}

private fun SearchFollowedShows.toWatchlistShowInfo(nextEpisodeMap: Map<Long, NextEpisodeWithShow>): WatchlistShowInfo {
    val watched = watched_count
    val total = total_episode_count
    val progress = if (total > 0) watched.toFloat() / total else 0f
    val nextEp = nextEpisodeMap[id.id]
    return WatchlistShowInfo(
        tmdbId = id.id,
        title = name,
        posterImageUrl = poster_path,
        status = status,
        year = first_air_date,
        seasonCount = season_count ?: 0,
        episodeCount = episode_count ?: 0,
        episodesWatched = watched,
        totalEpisodesTracked = total,
        watchProgress = progress,
        lastWatchedAt = nextEp?.lastWatchedAt,
        nextEpisode = nextEp?.toNextEpisodeInfo(),
    )
}

private fun NextEpisodeWithShow.toNextEpisodeInfo(): NextEpisodeInfo {
    return NextEpisodeInfo(
        episodeId = episodeId,
        episodeTitle = episodeName ?: "",
        seasonNumber = seasonNumber,
        episodeNumber = episodeNumber,
        stillPath = stillPath,
        airDate = airDate,
    )
}

private fun List<WatchlistShowInfo>.groupBySections(currentTimeMillis: Long): WatchlistSections {
    val threeWeeksAgo = currentTimeMillis - THREE_WEEKS_MILLIS

    val watchNext = mutableListOf<WatchlistShowInfo>()
    val stale = mutableListOf<WatchlistShowInfo>()

    forEach { item ->
        val allEpisodesWatched = item.totalEpisodesTracked > 0 &&
            item.episodesWatched >= item.totalEpisodesTracked
        val isCompleted = item.nextEpisode == null && allEpisodesWatched
        if (isCompleted) return@forEach

        val lastWatched = item.lastWatchedAt ?: 0L
        if (lastWatched in 1..<threeWeeksAgo) {
            stale.add(item)
        } else {
            watchNext.add(item)
        }
    }

    return WatchlistSections(
        watchNext = watchNext,
        stale = stale,
    )
}
