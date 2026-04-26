package com.thomaskioko.tvmaniac.presentation.episodedetail

import com.thomaskioko.tvmaniac.db.EpisodeById
import com.thomaskioko.tvmaniac.espisodedetails.nav.model.ScreenSource
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.api.Localizer
import kotlinx.collections.immutable.toImmutableList

internal fun EpisodeById.toState(
    source: ScreenSource,
    localizer: Localizer,
): EpisodeDetailSheetState {
    val isWatched = is_watched != 0L
    return EpisodeDetailSheetState(
        isLoading = false,
        episodeTitle = title,
        showName = show_name,
        seasonEpisodeNumber = "S${season_number}E$episode_number",
        imageUrl = image_url,
        overview = overview.ifBlank { null },
        rating = ratings.takeIf { it > 0 },
        voteCount = vote_count.takeIf { it > 0 },
        isWatched = isWatched,
        availableActions = availableActions(source, isWatched, localizer),
    )
}

private fun availableActions(
    source: ScreenSource,
    isWatched: Boolean,
    localizer: Localizer,
) = buildList {
    add(EpisodeSheetActionItem.TOGGLE_WATCHED.toUi(isWatched, localizer))
    if (source != ScreenSource.SEASON_DETAILS) {
        add(EpisodeSheetActionItem.OPEN_SHOW.toUi(isWatched, localizer))
        add(EpisodeSheetActionItem.OPEN_SEASON.toUi(isWatched, localizer))
        add(EpisodeSheetActionItem.UNFOLLOW.toUi(isWatched, localizer))
    }
}.toImmutableList()

private fun EpisodeSheetActionItem.toUi(
    isWatched: Boolean,
    localizer: Localizer,
): EpisodeSheetActionUi = EpisodeSheetActionUi(
    item = this,
    label = localizer.getString(labelKey(isWatched)),
)

private fun EpisodeSheetActionItem.labelKey(isWatched: Boolean): StringResourceKey = when (this) {
    EpisodeSheetActionItem.TOGGLE_WATCHED ->
        if (isWatched) StringResourceKey.LabelEpisodeActionMarkUnwatched else StringResourceKey.LabelEpisodeActionMarkWatched
    EpisodeSheetActionItem.OPEN_SHOW -> StringResourceKey.LabelEpisodeActionOpenShow
    EpisodeSheetActionItem.OPEN_SEASON -> StringResourceKey.LabelEpisodeActionOpenSeason
    EpisodeSheetActionItem.UNFOLLOW -> StringResourceKey.LabelEpisodeActionUnfollowShow
}
