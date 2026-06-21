package com.thomaskioko.tvmaniac.discover.ui.section

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewWrapper
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.discover.presenter.catalog.CatalogShowClicked
import com.thomaskioko.tvmaniac.discover.presenter.catalog.DiscoverCatalogAction
import com.thomaskioko.tvmaniac.discover.presenter.catalog.DiscoverCatalogPresenter
import com.thomaskioko.tvmaniac.discover.presenter.catalog.DiscoverCatalogState
import com.thomaskioko.tvmaniac.discover.presenter.catalog.PopularMoreClicked
import com.thomaskioko.tvmaniac.discover.presenter.catalog.TopRatedMoreClicked
import com.thomaskioko.tvmaniac.discover.presenter.catalog.TrendingMoreClicked
import com.thomaskioko.tvmaniac.discover.presenter.catalog.UpcomingMoreClicked
import com.thomaskioko.tvmaniac.discover.ui.component.HorizontalRowContent
import com.thomaskioko.tvmaniac.discover.ui.discoverCatalogContentSuccess
import com.thomaskioko.tvmaniac.testtags.discover.DiscoverTestTags

@Suppress("ktlint:tvmaniac:compose-screen-needs-codegen-annotation")
@Composable
public fun DiscoverCatalogSection(presenter: DiscoverCatalogPresenter) {
    val state by presenter.state.collectAsState()
    DiscoverCatalogSection(
        state = state,
        onAction = presenter::dispatch,
    )
}

@Composable
internal fun DiscoverCatalogSection(
    state: DiscoverCatalogState,
    onAction: (DiscoverCatalogAction) -> Unit,
) {
    Column(modifier = Modifier.testTag(DiscoverTestTags.CATALOG_SECTION_TEST_TAG)) {
        HorizontalRowContent(
            modifier = Modifier.testTag(DiscoverTestTags.ROW_KEY_TRENDING),
            category = state.trendingTitle,
            rowKey = DiscoverTestTags.ROW_KEY_TRENDING,
            tvShows = state.trendingShows,
            onItemClicked = { onAction(CatalogShowClicked(it)) },
            onMoreClicked = { onAction(TrendingMoreClicked) },
        )
        HorizontalRowContent(
            modifier = Modifier.testTag(DiscoverTestTags.ROW_KEY_UPCOMING),
            category = state.upcomingTitle,
            rowKey = DiscoverTestTags.ROW_KEY_UPCOMING,
            tvShows = state.upcomingShows,
            onItemClicked = { onAction(CatalogShowClicked(it)) },
            onMoreClicked = { onAction(UpcomingMoreClicked) },
        )
        HorizontalRowContent(
            modifier = Modifier.testTag(DiscoverTestTags.ROW_KEY_POPULAR),
            category = state.popularTitle,
            rowKey = DiscoverTestTags.ROW_KEY_POPULAR,
            tvShows = state.popularShows,
            onItemClicked = { onAction(CatalogShowClicked(it)) },
            onMoreClicked = { onAction(PopularMoreClicked) },
        )
        HorizontalRowContent(
            modifier = Modifier.testTag(DiscoverTestTags.ROW_KEY_TOP_RATED),
            category = state.topRatedTitle,
            rowKey = DiscoverTestTags.ROW_KEY_TOP_RATED,
            tvShows = state.topRatedShows,
            onItemClicked = { onAction(CatalogShowClicked(it)) },
            onMoreClicked = { onAction(TopRatedMoreClicked) },
        )
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun DiscoverCatalogSectionPreview() {
    DiscoverCatalogSection(
        state = discoverCatalogContentSuccess,
        onAction = {},
    )
}
