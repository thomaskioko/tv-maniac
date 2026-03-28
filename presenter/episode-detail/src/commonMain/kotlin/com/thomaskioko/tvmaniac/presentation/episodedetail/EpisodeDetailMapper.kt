package com.thomaskioko.tvmaniac.presentation.episodedetail

import com.thomaskioko.tvmaniac.db.EpisodeById
import kotlinx.collections.immutable.toImmutableList

internal fun EpisodeById.toState(source: ScreenSource): EpisodeDetailSheetState =
    EpisodeDetailSheetState(
        isLoading = false,
        episodeTitle = title,
        showName = show_name,
        seasonEpisodeNumber = "S${season_number}E$episode_number",
        imageUrl = image_url,
        overview = overview.ifBlank { null },
        rating = ratings.takeIf { it > 0 },
        voteCount = vote_count.takeIf { it > 0 },
        isWatched = is_watched != 0L,
        availableActions = availableActions(source),
    )

private fun availableActions(source: ScreenSource) = buildList {
    add(EpisodeSheetActionItem.TOGGLE_WATCHED)
    if (source != ScreenSource.SEASON_DETAILS) {
        add(EpisodeSheetActionItem.OPEN_SHOW)
        add(EpisodeSheetActionItem.OPEN_SEASON)
        add(EpisodeSheetActionItem.UNFOLLOW)
    }
}.toImmutableList()
