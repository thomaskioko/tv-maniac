package com.thomaskioko.tvmaniac.discover.ui.section

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewWrapper
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.discover.presenter.startwatching.DiscoverStartWatchingAction
import com.thomaskioko.tvmaniac.discover.presenter.startwatching.DiscoverStartWatchingPresenter
import com.thomaskioko.tvmaniac.discover.presenter.startwatching.DiscoverStartWatchingState
import com.thomaskioko.tvmaniac.discover.presenter.startwatching.StartWatchingItemClicked
import com.thomaskioko.tvmaniac.discover.presenter.startwatching.StartWatchingMoreClicked
import com.thomaskioko.tvmaniac.discover.ui.component.HorizontalRowContent
import com.thomaskioko.tvmaniac.discover.ui.discoverStartWatchingContentSuccess
import com.thomaskioko.tvmaniac.testtags.discover.DiscoverTestTags

@Suppress("ktlint:tvmaniac:compose-screen-needs-codegen-annotation")
@Composable
public fun DiscoverStartWatchingSection(presenter: DiscoverStartWatchingPresenter) {
    val state by presenter.state.collectAsState()
    DiscoverStartWatchingSection(
        state = state,
        onAction = presenter::dispatch,
    )
}

@Composable
internal fun DiscoverStartWatchingSection(
    state: DiscoverStartWatchingState,
    onAction: (DiscoverStartWatchingAction) -> Unit,
) {
    HorizontalRowContent(
        modifier = Modifier.testTag(DiscoverTestTags.ROW_KEY_START_WATCHING),
        category = state.startWatchingTitle,
        rowKey = DiscoverTestTags.ROW_KEY_START_WATCHING,
        tvShows = state.startWatchingShows,
        onItemClicked = { onAction(StartWatchingItemClicked(it)) },
        onMoreClicked = { onAction(StartWatchingMoreClicked) },
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun DiscoverStartWatchingSectionPreview() {
    DiscoverStartWatchingSection(
        state = discoverStartWatchingContentSuccess,
        onAction = {},
    )
}
