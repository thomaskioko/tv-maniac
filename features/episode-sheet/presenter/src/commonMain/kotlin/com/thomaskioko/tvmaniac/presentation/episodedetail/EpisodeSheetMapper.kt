package com.thomaskioko.tvmaniac.presentation.episodedetail

import com.thomaskioko.tvmaniac.db.EpisodeById
import com.thomaskioko.tvmaniac.espisodedetails.nav.model.ScreenSource
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.api.Localizer
import kotlinx.collections.immutable.toImmutableList

internal fun EpisodeById.toState(
    source: ScreenSource,
    localizer: Localizer,
    multiplePlaysEnabled: Boolean,
    rewatches: Long,
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
        playCount = if (isWatched) (rewatches + 1).toInt() else null,
        availableActions = availableActions(source, isWatched, multiplePlaysEnabled, localizer),
    )
}

internal fun removeWatchConfirmation(
    episodeTitle: String,
    localizer: Localizer,
): RemoveWatchConfirmation = RemoveWatchConfirmation(
    title = localizer.getString(StringResourceKey.LabelRemoveWatchConfirmTitle),
    message = localizer.getString(StringResourceKey.LabelRemoveWatchConfirmMessage, episodeTitle),
    confirmLabel = localizer.getString(StringResourceKey.DialogButtonYes),
    dismissLabel = localizer.getString(StringResourceKey.DialogButtonNo),
)

private fun availableActions(
    source: ScreenSource,
    isWatched: Boolean,
    multiplePlaysEnabled: Boolean,
    localizer: Localizer,
) = buildList {
    if (!isWatched || multiplePlaysEnabled) {
        add(EpisodeSheetActionItem.MARK_WATCHED.toUi(isWatched, localizer))
    }
    if (isWatched) {
        add(EpisodeSheetActionItem.MARK_UNWATCHED.toUi(isWatched, localizer))
    }
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
    EpisodeSheetActionItem.MARK_WATCHED ->
        if (isWatched) StringResourceKey.LabelActionWatchAgain else StringResourceKey.LabelEpisodeActionMarkWatched
    EpisodeSheetActionItem.MARK_UNWATCHED -> StringResourceKey.LabelEpisodeActionMarkUnwatched
    EpisodeSheetActionItem.OPEN_SHOW -> StringResourceKey.LabelEpisodeActionOpenShow
    EpisodeSheetActionItem.OPEN_SEASON -> StringResourceKey.LabelEpisodeActionOpenSeason
    EpisodeSheetActionItem.UNFOLLOW -> StringResourceKey.LabelEpisodeActionUnfollowShow
}
