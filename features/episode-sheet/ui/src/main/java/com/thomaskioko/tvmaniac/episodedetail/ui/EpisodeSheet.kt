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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.presentation.episodedetail.EpisodeDetailSheetState
import com.thomaskioko.tvmaniac.presentation.episodedetail.EpisodeSheetAction
import com.thomaskioko.tvmaniac.presentation.episodedetail.EpisodeSheetActionItem
import com.thomaskioko.tvmaniac.presentation.episodedetail.EpisodeSheetActionUi
import com.thomaskioko.tvmaniac.presentation.episodedetail.EpisodeSheetPresenter
import com.thomaskioko.tvmaniac.testtags.episodesheet.EpisodeSheetTestTags
import io.github.thomaskioko.codegen.annotations.SheetUi
import kotlinx.collections.immutable.persistentListOf

@SheetUi(presenter = EpisodeSheetPresenter::class, parentScope = ActivityScope::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun EpisodeSheet(
    presenter: EpisodeSheetPresenter,
    modifier: Modifier = Modifier,
) {
    val state by presenter.state.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (!state.isLoading) {
        EpisodeDetailBottomSheet(
            episode = state.toEpisodeDetailInfo(),
            sheetState = sheetState,
            onDismiss = { presenter.dispatch(EpisodeSheetAction.Dismiss) },
            modifier = modifier,
            actions = if (state.availableActions.isEmpty()) {
                null
            } else {
                { EpisodeSheetActions(state, presenter::dispatch) }
            },
        )
    }
}

@Composable
internal fun EpisodeDetailContent(
    state: EpisodeDetailSheetState,
    modifier: Modifier = Modifier,
    onAction: (EpisodeSheetAction) -> Unit = {},
) {
    EpisodeDetailSheetContent(
        episode = state.toEpisodeDetailInfo(),
        modifier = modifier,
        actions = if (state.availableActions.isEmpty()) {
            null
        } else {
            { EpisodeSheetActions(state, onAction) }
        },
    )
}

@Composable
private fun EpisodeSheetActions(
    state: EpisodeDetailSheetState,
    onAction: (EpisodeSheetAction) -> Unit,
) {
    state.availableActions.forEach { action ->
        SheetActionItem(
            modifier = Modifier.testTag(EpisodeSheetTestTags.actionItem(action.item.name)),
            icon = action.item.icon,
            label = action.label,
            onClick = { onAction(action.item.toAction()) },
        )
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

private val EpisodeSheetActionItem.icon: ImageVector
    get() = when (this) {
        EpisodeSheetActionItem.TOGGLE_WATCHED -> Icons.Outlined.Check
        EpisodeSheetActionItem.OPEN_SHOW -> Icons.Outlined.Tv
        EpisodeSheetActionItem.OPEN_SEASON -> Icons.Outlined.Movie
        EpisodeSheetActionItem.UNFOLLOW -> Icons.Outlined.LinkOff
    }

private fun EpisodeSheetActionItem.toAction(): EpisodeSheetAction = when (this) {
    EpisodeSheetActionItem.TOGGLE_WATCHED -> EpisodeSheetAction.ToggleWatched
    EpisodeSheetActionItem.OPEN_SHOW -> EpisodeSheetAction.OpenShow
    EpisodeSheetActionItem.OPEN_SEASON -> EpisodeSheetAction.OpenSeason
    EpisodeSheetActionItem.UNFOLLOW -> EpisodeSheetAction.Unfollow
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
                    EpisodeSheetActionUi(EpisodeSheetActionItem.TOGGLE_WATCHED, "Mark watched"),
                    EpisodeSheetActionUi(EpisodeSheetActionItem.OPEN_SHOW, "Open show"),
                    EpisodeSheetActionUi(EpisodeSheetActionItem.OPEN_SEASON, "Open season"),
                    EpisodeSheetActionUi(EpisodeSheetActionItem.UNFOLLOW, "Unfollow show"),
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
                    EpisodeSheetActionUi(EpisodeSheetActionItem.TOGGLE_WATCHED, "Mark unwatched"),
                    EpisodeSheetActionUi(EpisodeSheetActionItem.OPEN_SHOW, "Open show"),
                    EpisodeSheetActionUi(EpisodeSheetActionItem.OPEN_SEASON, "Open season"),
                    EpisodeSheetActionUi(EpisodeSheetActionItem.UNFOLLOW, "Unfollow show"),
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
                    EpisodeSheetActionUi(EpisodeSheetActionItem.TOGGLE_WATCHED, "Mark watched"),
                ),
            ),
        )
    }
}
