package com.thomaskioko.tvmaniac.domain.watchlist

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.domain.watchlist.model.UpNextEpisodeInfo
import com.thomaskioko.tvmaniac.domain.watchlist.model.UpNextSections
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.episodes.api.model.NextEpisodeWithShow
import com.thomaskioko.tvmaniac.shows.api.WatchlistRepository
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import me.tatarka.inject.annotations.Inject

private const val SEVEN_DAYS_MILLIS = 7 * 24 * 60 * 60 * 1000L

@Inject
public class ObserveUpNextSectionsInteractor(
    private val watchlistRepository: WatchlistRepository,
    private val episodeRepository: EpisodeRepository,
    private val dateTimeProvider: DateTimeProvider,
) : SubjectInteractor<String, UpNextSections>() {

    override fun createObservable(params: String): Flow<UpNextSections> {
        return combine(
            episodeRepository.observeNextEpisodesForWatchlist(),
            watchlistRepository.observeWatchlist(),
        ) { episodes, watchlist ->
            val currentTime = dateTimeProvider.nowMillis()
            val watchlistMap = watchlist.associate {
                it.show_id.id to (it.total_episode_count - it.watched_count).toInt()
            }

            val searchFiltered = if (params.isNotBlank()) {
                episodes.filter { it.showName.contains(params, ignoreCase = true) }
            } else {
                episodes
            }

            // Filter out episodes that haven't aired yet
            val airedEpisodes = searchFiltered.filter { episode ->
                val airDate = episode.airDate
                if (airDate == null) {
                    false // Unknown air date - don't show
                } else {
                    val daysUntilAir = dateTimeProvider.calculateDaysUntilAir(airDate)
                    daysUntilAir == null || daysUntilAir <= 0 // Has aired
                }
            }

            airedEpisodes
                .map { episode ->
                    val remaining = watchlistMap[episode.showId] ?: 0
                    episode.toUpNextEpisodeInfo(remaining)
                }.groupBySections(currentTime)
        }
    }
}

private fun NextEpisodeWithShow.toUpNextEpisodeInfo(remainingEpisodes: Int): UpNextEpisodeInfo {
    return UpNextEpisodeInfo(
        showId = showId,
        showName = showName,
        showPoster = showPoster,
        episodeId = episodeId,
        episodeTitle = episodeName,
        seasonId = seasonId,
        seasonNumber = seasonNumber,
        episodeNumber = episodeNumber,
        runtime = runtime,
        stillImage = stillPath,
        overview = overview,
        airDate = airDate,
        remainingEpisodes = remainingEpisodes,
        lastWatchedAt = lastWatchedAt,
    )
}

private fun List<UpNextEpisodeInfo>.groupBySections(currentTimeMillis: Long): UpNextSections {
    val sevenDaysAgo = currentTimeMillis - SEVEN_DAYS_MILLIS

    val watchNext = mutableListOf<UpNextEpisodeInfo>()
    val stale = mutableListOf<UpNextEpisodeInfo>()

    forEach { item ->
        val lastWatched = item.lastWatchedAt ?: 0L
        if (lastWatched in 1..<sevenDaysAgo) {
            stale.add(item)
        } else {
            watchNext.add(item)
        }
    }

    return UpNextSections(
        watchNext = watchNext,
        stale = stale,
    )
}
