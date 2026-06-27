package com.thomaskioko.tvmaniac.showdetails.ui.section

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewWrapper
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.presenter.showdetails.seasonsepisodes.ShowDetailsMarkEpisodeUnwatched
import com.thomaskioko.tvmaniac.presenter.showdetails.seasonsepisodes.ShowDetailsMarkEpisodeWatched
import com.thomaskioko.tvmaniac.presenter.showdetails.seasonsepisodes.ShowDetailsSeasonClicked
import com.thomaskioko.tvmaniac.presenter.showdetails.seasonsepisodes.ShowDetailsSeasonsEpisodesAction
import com.thomaskioko.tvmaniac.presenter.showdetails.seasonsepisodes.ShowDetailsSeasonsEpisodesPresenter
import com.thomaskioko.tvmaniac.presenter.showdetails.seasonsepisodes.ShowDetailsSeasonsEpisodesState
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowSeasonDetailsParam
import com.thomaskioko.tvmaniac.showdetails.ui.components.ContinueTrackingSection
import com.thomaskioko.tvmaniac.showdetails.ui.components.WatchProgressSection
import com.thomaskioko.tvmaniac.showdetails.ui.previewSeasonsEpisodesState
import com.thomaskioko.tvmaniac.testtags.showdetails.ShowDetailsTestTags

@Composable
internal fun ShowDetailsSeasonEpisodesSection(
    presenter: ShowDetailsSeasonsEpisodesPresenter,
    status: String?,
) {
    val state by presenter.state.collectAsState()
    ShowDetailsSeasonEpisodesSection(
        state = state,
        status = status,
        onAction = presenter::dispatch,
    )
}

@Composable
internal fun ShowDetailsSeasonEpisodesSection(
    state: ShowDetailsSeasonsEpisodesState,
    status: String?,
    onAction: (ShowDetailsSeasonsEpisodesAction) -> Unit,
) {
    ContinueTrackingSection(
        modifier = Modifier.testTag(ShowDetailsTestTags.CONTINUE_TRACKING_SECTION_TEST_TAG),
        episodes = state.continueTrackingEpisodes,
        scrollIndex = state.continueTrackingScrollIndex,
        updatingEpisodeIds = state.updatingEpisodeIds,
        onMarkWatched = { episode ->
            if (episode.isWatched) {
                onAction(
                    ShowDetailsMarkEpisodeUnwatched(
                        showId = episode.showId,
                        episodeId = episode.episodeId,
                    ),
                )
            } else {
                onAction(
                    ShowDetailsMarkEpisodeWatched(
                        showId = episode.showId,
                        episodeId = episode.episodeId,
                        seasonNumber = episode.seasonNumber,
                        episodeNumber = episode.episodeNumber,
                    ),
                )
            }
        },
    )
    WatchProgressSection(
        modifier = Modifier.testTag(ShowDetailsTestTags.WATCH_PROGRESS_SECTION_TEST_TAG),
        status = status,
        watchedEpisodesCount = state.watchedEpisodesCount,
        totalEpisodesCount = state.totalEpisodesCount,
        seasonsList = state.seasonsList,
        selectedSeasonIndex = state.selectedSeasonIndex,
        showHeader = state.continueTrackingEpisodes.isEmpty(),
        onSeasonClicked = { index, season ->
            onAction(
                ShowDetailsSeasonClicked(
                    params = ShowSeasonDetailsParam(
                        showId = season.tvShowId,
                        seasonId = season.seasonId,
                        seasonNumber = season.seasonNumber,
                        selectedSeasonIndex = index,
                    ),
                ),
            )
        },
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun ShowDetailsSeasonEpisodesSectionPreview() {
    ShowDetailsSeasonEpisodesSection(
        state = previewSeasonsEpisodesState,
        status = "Returning Series",
        onAction = {},
    )
}
