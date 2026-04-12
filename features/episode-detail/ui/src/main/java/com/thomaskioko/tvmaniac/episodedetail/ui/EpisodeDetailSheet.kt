package com.thomaskioko.tvmaniac.episodedetail.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.LinkOff
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.presentation.episodedetail.EpisodeDetailSheetAction
import com.thomaskioko.tvmaniac.presentation.episodedetail.EpisodeDetailSheetPresenter
import com.thomaskioko.tvmaniac.presentation.episodedetail.EpisodeDetailSheetState
import com.thomaskioko.tvmaniac.presentation.episodedetail.EpisodeSheetActionItem
import kotlinx.collections.immutable.persistentListOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun EpisodeDetailSheet(
    presenter: EpisodeDetailSheetPresenter,
    modifier: Modifier = Modifier,
) {
    val state by presenter.state.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (!state.isLoading) {
        EpisodeDetailBottomSheet(
            episode = state.toEpisodeDetailInfo(),
            sheetState = sheetState,
            onDismiss = { presenter.dispatch(EpisodeDetailSheetAction.Dismiss) },
            modifier = modifier,
            actions = state.toSheetActions { presenter.dispatch(it) },
        )
    }
}

@Composable
internal fun EpisodeDetailContent(
    state: EpisodeDetailSheetState,
    modifier: Modifier = Modifier,
    onAction: (EpisodeDetailSheetAction) -> Unit = {},
) {
    EpisodeDetailSheetContent(
        episode = state.toEpisodeDetailInfo(),
        modifier = modifier,
    ) {
        state.toSheetActions(onAction).forEach { action ->
            SheetActionItem(
                icon = action.icon,
                label = action.label,
                onClick = action.onClick,
            )
        }
    }
}

internal fun EpisodeDetailSheetState.toEpisodeDetailInfo() = EpisodeDetailInfo(
    title = episodeTitle,
    imageUrl = imageUrl,
    episodeInfo = buildString {
        append(seasonEpisodeNumber)
        if (showName.isNotBlank()) append(" • $showName")
    },
    overview = overview,
    rating = rating,
    voteCount = voteCount,
)

internal fun EpisodeDetailSheetState.toSheetActions(
    dispatch: (EpisodeDetailSheetAction) -> Unit,
): List<SheetAction> = availableActions.map { action ->
    when (action) {
        EpisodeSheetActionItem.TOGGLE_WATCHED -> SheetAction(
            icon = Icons.Outlined.Check,
            label = if (isWatched) "Mark unwatched" else "Mark watched",
            onClick = { dispatch(EpisodeDetailSheetAction.ToggleWatched) },
        )
        EpisodeSheetActionItem.OPEN_SHOW -> SheetAction(
            icon = Icons.Outlined.Tv,
            label = "Open show",
            onClick = { dispatch(EpisodeDetailSheetAction.OpenShow) },
        )
        EpisodeSheetActionItem.OPEN_SEASON -> SheetAction(
            icon = Icons.Outlined.Movie,
            label = "Open season",
            onClick = { dispatch(EpisodeDetailSheetAction.OpenSeason) },
        )
        EpisodeSheetActionItem.UNFOLLOW -> SheetAction(
            icon = Icons.Outlined.LinkOff,
            label = "Unfollow show",
            onClick = { dispatch(EpisodeDetailSheetAction.Unfollow) },
        )
    }
}

@ThemePreviews
@Composable
private fun EpisodeDetailContentAllActionsPreview() {
    TvManiacTheme {
        EpisodeDetailContent(
            state = EpisodeDetailSheetState(
                isLoading = false,
                episodeTitle = "The Walking Dead: Daryl Dixon",
                showName = "The Walking Dead",
                seasonEpisodeNumber = "S02E01",
                overview = "Daryl washes ashore in France and struggles to piece together how he got there and why.",
                rating = 8.5,
                voteCount = 1234,
                isWatched = false,
                availableActions = persistentListOf(
                    EpisodeSheetActionItem.TOGGLE_WATCHED,
                    EpisodeSheetActionItem.OPEN_SHOW,
                    EpisodeSheetActionItem.OPEN_SEASON,
                    EpisodeSheetActionItem.UNFOLLOW,
                ),
            ),
        )
    }
}

@ThemePreviews
@Composable
private fun EpisodeDetailContentWatchedPreview() {
    TvManiacTheme {
        EpisodeDetailContent(
            state = EpisodeDetailSheetState(
                isLoading = false,
                episodeTitle = "Wednesday",
                showName = "Wednesday",
                seasonEpisodeNumber = "S02E03",
                overview = "Wednesday arrives at Nevermore Academy and begins investigating a series of mysterious events.",
                rating = 7.9,
                voteCount = 856,
                isWatched = true,
                availableActions = persistentListOf(
                    EpisodeSheetActionItem.TOGGLE_WATCHED,
                    EpisodeSheetActionItem.OPEN_SHOW,
                    EpisodeSheetActionItem.OPEN_SEASON,
                    EpisodeSheetActionItem.UNFOLLOW,
                ),
            ),
        )
    }
}

@ThemePreviews
@Composable
private fun EpisodeDetailContentSeasonDetailsPreview() {
    TvManiacTheme {
        EpisodeDetailContent(
            state = EpisodeDetailSheetState(
                isLoading = false,
                episodeTitle = "House of the Dragon",
                showName = "House of the Dragon",
                seasonEpisodeNumber = "S03E01",
                overview = "King Viserys hosts a tournament to celebrate the birth of his heir.",
                isWatched = false,
                availableActions = persistentListOf(
                    EpisodeSheetActionItem.TOGGLE_WATCHED,
                ),
            ),
        )
    }
}
