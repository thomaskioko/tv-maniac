package com.thomaskioko.tvmaniac.discover.ui.section

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.discover.presenter.featured.DiscoverFeaturedAction
import com.thomaskioko.tvmaniac.discover.presenter.featured.DiscoverFeaturedPresenter
import com.thomaskioko.tvmaniac.discover.presenter.featured.DiscoverFeaturedState
import com.thomaskioko.tvmaniac.discover.presenter.featured.FeaturedShowClicked
import com.thomaskioko.tvmaniac.discover.ui.component.DiscoverHeaderContent
import com.thomaskioko.tvmaniac.discover.ui.discoverFeaturedContentSuccess

@Suppress("ktlint:tvmaniac:compose-screen-needs-codegen-annotation")
@Composable
public fun DiscoverFeaturedSection(presenter: DiscoverFeaturedPresenter) {
    val state by presenter.state.collectAsState()
    val pagerState = rememberPagerState(pageCount = { state.featuredShows.size })
    DiscoverFeaturedSection(
        state = state,
        pagerState = pagerState,
        onAction = presenter::dispatch,
    )
}

@Composable
internal fun DiscoverFeaturedSection(
    state: DiscoverFeaturedState,
    pagerState: PagerState,
    onAction: (DiscoverFeaturedAction) -> Unit,
) {
    if (state.featuredShows.isEmpty()) {
        Spacer(modifier = Modifier.padding(top = 108.dp))
    } else {
        DiscoverHeaderContent(
            pagerState = pagerState,
            showList = state.featuredShows,
            onShowClicked = { onAction(FeaturedShowClicked(it)) },
        )
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun DiscoverFeaturedSectionPreview() {
    val pagerState = rememberPagerState(pageCount = { discoverFeaturedContentSuccess.featuredShows.size })
    DiscoverFeaturedSection(
        state = discoverFeaturedContentSuccess,
        pagerState = pagerState,
        onAction = {},
    )
}
