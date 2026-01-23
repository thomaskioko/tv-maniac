package com.thomaskioko.tvmaniac.domain.watchlist

import com.thomaskioko.tvmaniac.domain.watchlist.model.EpisodeBadge
import com.thomaskioko.tvmaniac.domain.watchlist.model.UpNextEpisodeInfo
import com.thomaskioko.tvmaniac.domain.watchlist.model.UpNextSections
import com.thomaskioko.tvmaniac.episodes.api.model.NextEpisodeWithShow
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import me.tatarka.inject.annotations.Inject

private const val SIXTEEN_DAYS_MILLIS: Long = 16 * 24 * 60 * 60 * 1000L

@Inject
public class UpNextSectionsMapper(
    private val dateTimeProvider: DateTimeProvider,
) {

    public fun map(episodes: List<NextEpisodeWithShow>): UpNextSections {
        val currentTime = dateTimeProvider.nowMillis()

        return episodes
            .map { episode ->
                val remaining = (episode.totalCount - episode.watchedCount).toInt()
                val badge = calculateBadge(
                    episodeNumber = episode.episodeNumber,
                    seasonNumber = episode.seasonNumber,
                    showYear = episode.showYear,
                    firstAired = episode.firstAired,
                    currentTimeMillis = currentTime,
                )
                episode.toUpNextEpisodeInfo(remaining, badge)
            }
            .filter { it.firstAired != null && it.firstAired <= currentTime }
            .groupBySections(currentTime)
    }

    private fun calculateBadge(
        episodeNumber: Long,
        seasonNumber: Long,
        showYear: String?,
        firstAired: Long?,
        currentTimeMillis: Long,
    ): EpisodeBadge {
        if (episodeNumber != 1L) return EpisodeBadge.NONE

        val currentYear = dateTimeProvider.currentYear().toString()

        return when {
            seasonNumber == 1L && showYear == currentYear -> EpisodeBadge.NEW
            seasonNumber > 1L && isRecent(firstAired, currentTimeMillis) -> EpisodeBadge.PREMIERE
            else -> EpisodeBadge.NONE
        }
    }

    private fun isRecent(firstAired: Long?, currentTimeMillis: Long): Boolean {
        if (firstAired == null) return false
        val sixteenDaysAgo = currentTimeMillis - SIXTEEN_DAYS_MILLIS
        return firstAired >= sixteenDaysAgo
    }

    private fun NextEpisodeWithShow.toUpNextEpisodeInfo(
        remainingEpisodes: Int,
        badge: EpisodeBadge,
    ): UpNextEpisodeInfo {
        return UpNextEpisodeInfo(
            showTraktId = showTraktId,
            showName = showName,
            showPoster = showPoster,
            episodeId = episodeId,
            episodeTitle = episodeName ?: "",
            episodeNumberFormatted = formatEpisodeNumber(seasonNumber, episodeNumber),
            seasonId = seasonId,
            seasonNumber = seasonNumber,
            episodeNumber = episodeNumber,
            formattedRuntime = runtime?.let { "$it min" },
            stillImage = stillPath,
            overview = overview ?: "",
            firstAired = firstAired,
            remainingEpisodes = remainingEpisodes,
            lastWatchedAt = lastWatchedAt,
            followedAt = followedAt,
            badge = badge,
        )
    }

    private fun formatEpisodeNumber(seasonNumber: Long, episodeNumber: Long): String {
        val season = seasonNumber.toString().padStart(2, '0')
        val episode = episodeNumber.toString().padStart(2, '0')
        return "S$season | E$episode"
    }

    private fun List<UpNextEpisodeInfo>.groupBySections(currentTimeMillis: Long): UpNextSections {
        val sixteenDaysAgo = currentTimeMillis - SIXTEEN_DAYS_MILLIS

        val watchNext = mutableListOf<UpNextEpisodeInfo>()
        val stale = mutableListOf<UpNextEpisodeInfo>()

        forEach { item ->
            val lastWatched = item.lastWatchedAt ?: 0L
            val followedAt = item.followedAt ?: 0L

            val isStale = when {
                lastWatched > 0 -> lastWatched < sixteenDaysAgo
                followedAt > 0 -> followedAt < sixteenDaysAgo
                else -> false
            }

            if (isStale) stale.add(item) else watchNext.add(item)
        }

        return UpNextSections(
            watchNext = watchNext,
            stale = stale,
        )
    }
}
