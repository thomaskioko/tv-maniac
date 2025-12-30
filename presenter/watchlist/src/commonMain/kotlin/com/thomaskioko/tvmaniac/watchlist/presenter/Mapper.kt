package com.thomaskioko.tvmaniac.watchlist.presenter

import com.thomaskioko.tvmaniac.db.SearchWatchlist
import com.thomaskioko.tvmaniac.db.Watchlists
import com.thomaskioko.tvmaniac.domain.watchlist.model.UpNextEpisodeInfo
import com.thomaskioko.tvmaniac.domain.watchlist.model.UpNextSections
import com.thomaskioko.tvmaniac.domain.watchlist.model.WatchlistSections
import com.thomaskioko.tvmaniac.domain.watchlist.model.WatchlistShowInfo
import com.thomaskioko.tvmaniac.episodes.api.model.NextEpisodeWithShow
import com.thomaskioko.tvmaniac.watchlist.presenter.model.EpisodeBadge
import com.thomaskioko.tvmaniac.watchlist.presenter.model.UpNextEpisodeItem
import com.thomaskioko.tvmaniac.watchlist.presenter.model.WatchlistItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList

private const val SEVEN_DAYS_MILLIS: Long = 7 * 24 * 60 * 60 * 1000L

public fun List<Watchlists>.entityToWatchlistShowList(
    lastWatchedMap: Map<Long, Long?> = emptyMap(),
): PersistentList<WatchlistItem> {
    return this.map {
        val watched = it.watched_count
        val total = it.total_episode_count
        val progress = if (total > 0) watched.toFloat() / total else 0f
        WatchlistItem(
            tmdbId = it.id.id,
            title = it.name,
            posterImageUrl = it.poster_path,
            status = it.status,
            year = it.first_air_date,
            seasonCount = it.season_count ?: 0,
            episodeCount = it.episode_count ?: 0,
            episodesWatched = watched,
            totalEpisodesTracked = total,
            watchProgress = progress,
            lastWatchedAt = lastWatchedMap[it.id.id],
        )
    }
        .toPersistentList()
}

public fun List<SearchWatchlist>.entityToWatchlistShowList(
    lastWatchedMap: Map<Long, Long?> = emptyMap(),
): ImmutableList<WatchlistItem> {
    return this.map {
        val watched = it.watched_count
        val total = it.total_episode_count
        val progress = if (total > 0) watched.toFloat() / total else 0f
        WatchlistItem(
            tmdbId = it.id.id,
            title = it.name,
            posterImageUrl = it.poster_path,
            status = it.status,
            year = it.first_air_date,
            seasonCount = it.season_count ?: 0,
            episodeCount = it.episode_count ?: 0,
            episodesWatched = watched,
            totalEpisodesTracked = total,
            watchProgress = progress,
            lastWatchedAt = lastWatchedMap[it.id.id],
        )
    }
        .toPersistentList()
}

public fun NextEpisodeWithShow.toUpNextEpisodeItem(
    currentTimeMillis: Long,
    remainingEpisodes: Int = 0,
): UpNextEpisodeItem {
    val badge = calculateBadge(episodeNumber, airDate, currentTimeMillis)
    return UpNextEpisodeItem(
        showId = showId,
        showName = showName,
        showPoster = showPoster,
        episodeId = episodeId,
        episodeTitle = episodeName,
        episodeNumberFormatted = "S${seasonNumber.toString().padStart(2, '0')} | E${episodeNumber.toString().padStart(2, '0')}",
        seasonId = seasonId,
        seasonNumber = seasonNumber,
        episodeNumber = episodeNumber,
        runtime = runtime?.let { "$it min" },
        stillImage = stillPath,
        overview = overview,
        badge = badge,
        remainingEpisodes = remainingEpisodes,
        lastWatchedAt = lastWatchedAt,
    )
}

private fun calculateBadge(
    episodeNumber: Long,
    airDate: String?,
    currentTimeMillis: Long,
): EpisodeBadge {
    airDate?.let { dateString ->
        val episodeAirMillis = parseAirDateToMillis(dateString)
        if (episodeAirMillis != null) {
            val sevenDaysAgo = currentTimeMillis - SEVEN_DAYS_MILLIS
            if (episodeAirMillis >= sevenDaysAgo) {
                return if (episodeNumber == 1L) EpisodeBadge.PREMIERE else EpisodeBadge.NEW
            }
        }
    }

    return EpisodeBadge.NONE
}

private fun parseAirDateToMillis(dateString: String): Long? {
    return try {
        val parts = dateString.split("-")
        if (parts.size == 3) {
            val year = parts[0].toInt()
            val month = parts[1].toInt()
            val day = parts[2].toInt()
            val daysSinceEpoch = calculateDaysSinceEpoch(year, month, day)
            daysSinceEpoch * 24 * 60 * 60 * 1000L
        } else {
            null
        }
    } catch (e: Exception) {
        null
    }
}

private fun calculateDaysSinceEpoch(year: Int, month: Int, day: Int): Long {
    var y = year
    var m = month
    if (m <= 2) {
        y -= 1
        m += 12
    }
    val era = if (y >= 0) y / 400 else (y - 399) / 400
    val yoe = y - era * 400
    val doy = (153 * (m + (if (m > 2) -3 else 9)) + 2) / 5 + day - 1
    val doe = yoe * 365 + yoe / 4 - yoe / 100 + doy
    return (era * 146097L + doe - 719468)
}

public data class SectionedEpisodes(
    val watchNext: ImmutableList<UpNextEpisodeItem>,
    val stale: ImmutableList<UpNextEpisodeItem>,
)

public fun List<UpNextEpisodeItem>.groupBySections(currentTimeMillis: Long): SectionedEpisodes {
    val sevenDaysAgo = currentTimeMillis - SEVEN_DAYS_MILLIS

    val watchNext = mutableListOf<UpNextEpisodeItem>()
    val stale = mutableListOf<UpNextEpisodeItem>()

    forEach { item ->
        val lastWatched = item.lastWatchedAt ?: 0L
        if (lastWatched in 1..<sevenDaysAgo) {
            stale.add(item)
        } else {
            watchNext.add(item)
        }
    }

    return SectionedEpisodes(
        watchNext = watchNext.toImmutableList(),
        stale = stale.toImmutableList(),
    )
}

public data class SectionedItems(
    val watchNext: ImmutableList<WatchlistItem>,
    val stale: ImmutableList<WatchlistItem>,
)

public fun List<WatchlistItem>.groupItemsBySections(currentTimeMillis: Long): SectionedItems {
    val sevenDaysAgo = currentTimeMillis - SEVEN_DAYS_MILLIS

    val watchNext = mutableListOf<WatchlistItem>()
    val stale = mutableListOf<WatchlistItem>()

    forEach { item ->
        val lastWatched = item.lastWatchedAt ?: 0L
        if (lastWatched > 0L && lastWatched < sevenDaysAgo) {
            stale.add(item)
        } else {
            watchNext.add(item)
        }
    }

    return SectionedItems(
        watchNext = watchNext.toImmutableList(),
        stale = stale.toImmutableList(),
    )
}

public fun WatchlistSections.toPresenter(): SectionedItems = SectionedItems(
    watchNext = watchNext.map { it.toPresenter() }.toImmutableList(),
    stale = stale.map { it.toPresenter() }.toImmutableList(),
)

public fun WatchlistShowInfo.toPresenter(): WatchlistItem = WatchlistItem(
    tmdbId = tmdbId,
    title = title,
    posterImageUrl = posterImageUrl,
    status = status,
    year = year,
    seasonCount = seasonCount,
    episodeCount = episodeCount,
    episodesWatched = episodesWatched,
    totalEpisodesTracked = totalEpisodesTracked,
    watchProgress = watchProgress,
    lastWatchedAt = lastWatchedAt,
)

public fun UpNextSections.toPresenter(currentTimeMillis: Long): SectionedEpisodes = SectionedEpisodes(
    watchNext = watchNext.map { it.toPresenter(currentTimeMillis) }.toImmutableList(),
    stale = stale.map { it.toPresenter(currentTimeMillis) }.toImmutableList(),
)

public fun UpNextEpisodeInfo.toPresenter(currentTimeMillis: Long): UpNextEpisodeItem {
    val badge = calculateBadge(episodeNumber, airDate, currentTimeMillis)
    return UpNextEpisodeItem(
        showId = showId,
        showName = showName,
        showPoster = showPoster,
        episodeId = episodeId,
        episodeTitle = episodeTitle ?: "",
        episodeNumberFormatted = "S${seasonNumber.toString().padStart(2, '0')} | E${episodeNumber.toString().padStart(2, '0')}",
        seasonId = seasonId,
        seasonNumber = seasonNumber,
        episodeNumber = episodeNumber,
        runtime = runtime?.let { "$it min" },
        stillImage = stillImage,
        overview = overview ?: "",
        badge = badge,
        remainingEpisodes = remainingEpisodes,
        lastWatchedAt = lastWatchedAt,
    )
}
