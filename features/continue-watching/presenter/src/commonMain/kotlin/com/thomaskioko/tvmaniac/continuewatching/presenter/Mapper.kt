package com.thomaskioko.tvmaniac.continuewatching.presenter

import com.thomaskioko.tvmaniac.continuewatching.presenter.model.ContinueWatchingItem
import com.thomaskioko.tvmaniac.continuewatching.presenter.model.EpisodeBadge
import com.thomaskioko.tvmaniac.continuewatching.presenter.model.NextEpisodeItem
import com.thomaskioko.tvmaniac.continuewatching.presenter.model.SectionedEpisodes
import com.thomaskioko.tvmaniac.continuewatching.presenter.model.SectionedItems
import com.thomaskioko.tvmaniac.continuewatching.presenter.model.UpNextEpisodeItem
import com.thomaskioko.tvmaniac.db.FollowedShows
import com.thomaskioko.tvmaniac.db.SearchFollowedShows
import com.thomaskioko.tvmaniac.domain.continuewatching.model.NextEpisodeInfo
import com.thomaskioko.tvmaniac.domain.continuewatching.model.UpNextEpisodeInfo
import com.thomaskioko.tvmaniac.domain.continuewatching.model.UpNextSections
import com.thomaskioko.tvmaniac.domain.continuewatching.model.WatchlistSections
import com.thomaskioko.tvmaniac.domain.continuewatching.model.WatchlistShowInfo
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import com.thomaskioko.tvmaniac.domain.continuewatching.model.EpisodeBadge as DomainEpisodeBadge

public fun List<FollowedShows>.entityToWatchlistShowList(
    lastWatchedMap: Map<Long, Long?> = emptyMap(),
): PersistentList<ContinueWatchingItem> {
    return this.map {
        val watched = it.watched_count
        val total = it.total_episode_count
        val progress = if (total > 0) watched.toFloat() / total else 0f
        ContinueWatchingItem(
            showId = it.show_trakt_id,
            title = it.name,
            posterImageUrl = it.poster_path,
            status = it.status,
            year = it.year,
            seasonCount = it.season_count ?: 0,
            episodeCount = it.episode_count ?: 0,
            episodesWatched = watched,
            totalEpisodesTracked = total,
            watchProgress = progress,
            lastWatchedAt = lastWatchedMap[it.show_trakt_id],
        )
    }
        .toPersistentList()
}

public fun List<SearchFollowedShows>.entityToWatchlistShowList(
    lastWatchedMap: Map<Long, Long?> = emptyMap(),
): ImmutableList<ContinueWatchingItem> {
    return this.map {
        val watched = it.watched_count
        val total = it.total_episode_count
        val progress = if (total > 0) watched.toFloat() / total else 0f
        ContinueWatchingItem(
            showId = it.show_trakt_id,
            title = it.name,
            posterImageUrl = it.poster_path,
            status = it.status,
            year = it.year,
            seasonCount = it.season_count ?: 0,
            episodeCount = it.episode_count ?: 0,
            episodesWatched = watched,
            totalEpisodesTracked = total,
            watchProgress = progress,
            lastWatchedAt = lastWatchedMap[it.show_trakt_id],
        )
    }
        .toPersistentList()
}

internal fun WatchlistSections.toPresenter(): SectionedItems = SectionedItems(
    watchNext = watchNext.map { it.toPresenter() }.toImmutableList(),
    stale = stale.map { it.toPresenter() }.toImmutableList(),
)

internal fun WatchlistShowInfo.toPresenter(): ContinueWatchingItem = ContinueWatchingItem(
    showId = showId,
    title = title ?: "",
    posterImageUrl = posterImageUrl,
    status = status,
    year = year,
    seasonCount = seasonCount,
    episodeCount = episodeCount,
    episodesWatched = episodesWatched,
    totalEpisodesTracked = totalEpisodesTracked,
    watchProgress = watchProgress,
    lastWatchedAt = lastWatchedAt,
    nextEpisode = nextEpisode?.toPresenter(),
)

private fun NextEpisodeInfo.toPresenter(): NextEpisodeItem = NextEpisodeItem(
    episodeId = episodeId,
    episodeTitle = episodeTitle,
    episodeNumberFormatted = "S${seasonNumber.toString().padStart(2, '0')} | E${episodeNumber.toString().padStart(2, '0')}",
    seasonNumber = seasonNumber,
    episodeNumber = episodeNumber,
    stillPath = stillPath,
    firstAired = firstAired,
)

internal fun UpNextSections.toPresenter(): SectionedEpisodes = SectionedEpisodes(
    watchNext = watchNext.map { it.toPresenter() }.toImmutableList(),
    stale = stale.map { it.toPresenter() }.toImmutableList(),
)

internal fun UpNextEpisodeInfo.toPresenter(): UpNextEpisodeItem {
    return UpNextEpisodeItem(
        showId = showId,
        showName = showName,
        showPoster = showPoster,
        episodeId = episodeId,
        episodeTitle = episodeTitle,
        episodeNumberFormatted = episodeNumberFormatted,
        seasonId = seasonId,
        seasonNumber = seasonNumber,
        episodeNumber = episodeNumber,
        runtime = formattedRuntime,
        stillImage = stillImage,
        overview = overview,
        badge = badge.toPresenter(),
        remainingEpisodes = remainingEpisodes,
        lastWatchedAt = lastWatchedAt,
    )
}

private fun DomainEpisodeBadge.toPresenter(): EpisodeBadge? = when (this) {
    DomainEpisodeBadge.PREMIERE -> EpisodeBadge.PREMIERE
    DomainEpisodeBadge.NEW -> EpisodeBadge.NEW
    DomainEpisodeBadge.NONE -> null
}
