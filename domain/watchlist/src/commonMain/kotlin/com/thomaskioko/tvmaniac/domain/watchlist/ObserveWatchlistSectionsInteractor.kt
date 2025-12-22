package com.thomaskioko.tvmaniac.domain.watchlist

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.db.SearchWatchlist
import com.thomaskioko.tvmaniac.db.Watchlists
import com.thomaskioko.tvmaniac.domain.watchlist.model.WatchlistSections
import com.thomaskioko.tvmaniac.domain.watchlist.model.WatchlistShowInfo
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.shows.api.WatchlistRepository
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject
import kotlin.jvm.JvmName

private const val SEVEN_DAYS_MILLIS = 7 * 24 * 60 * 60 * 1000L

@Inject
class ObserveWatchlistSectionsInteractor(
    private val watchlistRepository: WatchlistRepository,
    private val episodeRepository: EpisodeRepository,
    private val dateTimeProvider: DateTimeProvider,
) : SubjectInteractor<String, WatchlistSections>() {

    override fun createObservable(params: String): Flow<WatchlistSections> {
        return episodeRepository.observeNextEpisodesForWatchlist()
            .flatMapLatest { episodes ->
                val lastWatchedMap = episodes.associate { it.showId to it.lastWatchedAt }

                if (params.isNotBlank()) {
                    watchlistRepository.searchWatchlistByQuery(params).map { list ->
                        list.toWatchlistSections(lastWatchedMap, dateTimeProvider.nowMillis())
                    }
                } else {
                    watchlistRepository.observeWatchlist().map { list ->
                        list.toWatchlistSections(lastWatchedMap, dateTimeProvider.nowMillis())
                    }
                }
            }
    }
}

private fun List<Watchlists>.toWatchlistSections(
    lastWatchedMap: Map<Long, Long?>,
    currentTimeMillis: Long,
): WatchlistSections {
    val items = map { it.toWatchlistShowInfo(lastWatchedMap) }
    return items.groupBySections(currentTimeMillis)
}

@JvmName("searchToWatchlistSections")
private fun List<SearchWatchlist>.toWatchlistSections(
    lastWatchedMap: Map<Long, Long?>,
    currentTimeMillis: Long,
): WatchlistSections {
    val items = map { it.toWatchlistShowInfo(lastWatchedMap) }
    return items.groupBySections(currentTimeMillis)
}

private fun Watchlists.toWatchlistShowInfo(lastWatchedMap: Map<Long, Long?>): WatchlistShowInfo {
    val watched = watched_count
    val total = total_episode_count
    val progress = if (total > 0) watched.toFloat() / total else 0f
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
        lastWatchedAt = lastWatchedMap[id.id],
    )
}

private fun SearchWatchlist.toWatchlistShowInfo(lastWatchedMap: Map<Long, Long?>): WatchlistShowInfo {
    val watched = watched_count
    val total = total_episode_count
    val progress = if (total > 0) watched.toFloat() / total else 0f
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
        lastWatchedAt = lastWatchedMap[id.id],
    )
}

private fun List<WatchlistShowInfo>.groupBySections(currentTimeMillis: Long): WatchlistSections {
    val sevenDaysAgo = currentTimeMillis - SEVEN_DAYS_MILLIS

    val watchNext = mutableListOf<WatchlistShowInfo>()
    val stale = mutableListOf<WatchlistShowInfo>()

    forEach { item ->
        val lastWatched = item.lastWatchedAt ?: 0L
        if (lastWatched > 0L && lastWatched < sevenDaysAgo) {
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
