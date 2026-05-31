package com.thomaskioko.tvmaniac.startwatching.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewWrapper
import com.thomaskioko.tvmaniac.compose.components.EmptyStateView
import com.thomaskioko.tvmaniac.compose.components.LoadingIndicator
import com.thomaskioko.tvmaniac.compose.components.PosterCard
import com.thomaskioko.tvmaniac.compose.components.SnackBarStyle
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.components.TvManiacSnackBarHost
import com.thomaskioko.tvmaniac.compose.theme.ImageDimens
import com.thomaskioko.tvmaniac.compose.theme.Layout
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_start_watching_empty
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.startwatching.presenter.RefreshStartWatching
import com.thomaskioko.tvmaniac.startwatching.presenter.StartWatchingAction
import com.thomaskioko.tvmaniac.startwatching.presenter.StartWatchingMessageShown
import com.thomaskioko.tvmaniac.startwatching.presenter.StartWatchingShowClicked
import com.thomaskioko.tvmaniac.startwatching.presenter.StartWatchingState
import com.thomaskioko.tvmaniac.startwatching.presenter.model.StartWatchingItem
import com.thomaskioko.tvmaniac.testtags.startwatching.StartWatchingTestTags
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun StartWatchingScreen(
    state: StartWatchingState,
    modifier: Modifier = Modifier,
    onAction: (StartWatchingAction) -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        PullToRefreshBox(
            modifier = Modifier.fillMaxSize(),
            isRefreshing = state.isRefreshing,
            onRefresh = { onAction(RefreshStartWatching()) },
        ) {
            when {
                state.showLoading -> LoadingIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag(StartWatchingTestTags.PROGRESS_INDICATOR),
                )

                state.isEmpty -> EmptyStateView(
                    title = label_start_watching_empty.resolve(LocalContext.current),
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag(StartWatchingTestTags.EMPTY_STATE),
                )

                else -> StartWatchingGrid(
                    items = state.items,
                    onShowClicked = { onAction(StartWatchingShowClicked(it)) },
                )
            }
        }

        TvManiacSnackBarHost(
            message = state.message?.message,
            style = SnackBarStyle.Error,
            onDismiss = { state.message?.let { onAction(StartWatchingMessageShown(it.id)) } },
        )
    }
}

@Composable
private fun StartWatchingGrid(
    items: ImmutableList<StartWatchingItem>,
    onShowClicked: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(Layout.posterColumns),
        verticalArrangement = Arrangement.spacedBy(ImageDimens.GridItemSpacing),
        horizontalArrangement = Arrangement.spacedBy(ImageDimens.GridItemSpacing),
        modifier = modifier
            .fillMaxSize()
            .testTag(StartWatchingTestTags.GRID)
            .padding(horizontal = ImageDimens.GridItemSpacing),
    ) {
        items(
            items = items,
            key = { it.traktId },
        ) { item ->
            PosterCard(
                imageUrl = item.posterImageUrl,
                title = item.title,
                onClick = { onShowClicked(item.traktId) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(StartWatchingTestTags.showCard(item.traktId)),
            )
        }
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun StartWatchingScreenPreview(
    @PreviewParameter(StartWatchingPreviewParameterProvider::class) state: StartWatchingState,
) {
    StartWatchingScreen(
        state = state,
        onAction = {},
    )
}
