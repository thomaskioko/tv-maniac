package com.thomaskioko.tvmaniac.discover.ui.section

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewWrapper
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.discover.presenter.upnext.DiscoverEpisodeLongPressed
import com.thomaskioko.tvmaniac.discover.presenter.upnext.DiscoverUpNextAction
import com.thomaskioko.tvmaniac.discover.presenter.upnext.DiscoverUpNextPresenter
import com.thomaskioko.tvmaniac.discover.presenter.upnext.DiscoverUpNextState
import com.thomaskioko.tvmaniac.discover.ui.component.NextEpisodesSection
import com.thomaskioko.tvmaniac.discover.ui.discoverUpNextContentSuccess
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_discover_up_next
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.testtags.discover.DiscoverTestTags

@Composable
public fun DiscoverUpNextSection(presenter: DiscoverUpNextPresenter) {
    val state by presenter.state.collectAsState()
    val context = LocalContext.current
    DiscoverUpNextSection(
        state = state,
        title = label_discover_up_next.resolve(context),
        onAction = presenter::dispatch,
    )
}

@Composable
internal fun DiscoverUpNextSection(
    state: DiscoverUpNextState,
    title: String,
    onAction: (DiscoverUpNextAction) -> Unit,
) {
    NextEpisodesSection(
        modifier = Modifier.testTag(DiscoverTestTags.UP_NEXT_SECTION_TEST_TAG),
        title = title,
        nextEpisodes = state.nextEpisodes,
        onEpisodeClick = { episode ->
            onAction(DiscoverEpisodeLongPressed(showId = episode.showId, episodeId = episode.episodeId))
        },
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun DiscoverUpNextSectionPreview() {
    DiscoverUpNextSection(
        state = discoverUpNextContentSuccess,
        title = "Up Next",
        onAction = {},
    )
}
