package com.thomaskioko.tvmaniac.continuewatching.presenter

import com.thomaskioko.tvmaniac.continuewatching.presenter.model.ContinueWatchingItem
import com.thomaskioko.tvmaniac.continuewatching.presenter.model.EpisodeBadge
import com.thomaskioko.tvmaniac.continuewatching.presenter.model.NextEpisodeItem
import com.thomaskioko.tvmaniac.continuewatching.presenter.model.SectionedEpisodes
import com.thomaskioko.tvmaniac.continuewatching.presenter.model.SectionedItems
import com.thomaskioko.tvmaniac.continuewatching.presenter.model.UpNextEpisodeItem
import com.thomaskioko.tvmaniac.domain.continuewatching.model.NextEpisodeInfo
import com.thomaskioko.tvmaniac.domain.continuewatching.model.UpNextEpisodeInfo
import com.thomaskioko.tvmaniac.domain.continuewatching.model.UpNextSections
import com.thomaskioko.tvmaniac.domain.continuewatching.model.WatchlistSections
import com.thomaskioko.tvmaniac.domain.continuewatching.model.WatchlistShowInfo
import kotlinx.collections.immutable.toImmutableList
import com.thomaskioko.tvmaniac.domain.continuewatching.model.EpisodeBadge as DomainEpisodeBadge

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
