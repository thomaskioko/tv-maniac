package com.thomaskioko.tvmaniac.showdetails.roborrazi

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.TvManiacBackground
import com.thomaskioko.tvmaniac.screenshottests.captureMultiDevice
import com.thomaskioko.tvmaniac.showdetails.ui.ShowDetailsScaffold
import com.thomaskioko.tvmaniac.showdetails.ui.previewCastState
import com.thomaskioko.tvmaniac.showdetails.ui.previewHeaderState
import com.thomaskioko.tvmaniac.showdetails.ui.previewHeaderStateRated
import com.thomaskioko.tvmaniac.showdetails.ui.previewHeaderStateSimkl
import com.thomaskioko.tvmaniac.showdetails.ui.previewHostState
import com.thomaskioko.tvmaniac.showdetails.ui.previewHostStateWithMessage
import com.thomaskioko.tvmaniac.showdetails.ui.previewProvidersState
import com.thomaskioko.tvmaniac.showdetails.ui.previewSeasonsEpisodesState
import com.thomaskioko.tvmaniac.showdetails.ui.previewSimilarState
import com.thomaskioko.tvmaniac.showdetails.ui.previewTrailersState
import com.thomaskioko.tvmaniac.showdetails.ui.section.ShowDetailsCastSection
import com.thomaskioko.tvmaniac.showdetails.ui.section.ShowDetailsHeaderSection
import com.thomaskioko.tvmaniac.showdetails.ui.section.ShowDetailsProvidersSection
import com.thomaskioko.tvmaniac.showdetails.ui.section.ShowDetailsSeasonEpisodesSection
import com.thomaskioko.tvmaniac.showdetails.ui.section.ShowDetailsSimilarSection
import com.thomaskioko.tvmaniac.showdetails.ui.section.ShowDetailsTrailersSection
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.LooperMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@LooperMode(LooperMode.Mode.PAUSED)
class ShowDetailsScreenScreenshotTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun showDetailsLoadedState() {
        composeTestRule.captureMultiDevice("ShowDetailsLoadedState") {
            TvManiacBackground {
                ShowDetailsScaffold(
                    hostState = previewHostState,
                    title = previewHeaderState.title,
                    isHeaderEmpty = false,
                    listState = LazyListState(),
                    onHostAction = {},
                ) {
                    item(key = "header") {
                        ShowDetailsHeaderSection(state = previewHeaderState, onAction = {})
                    }
                    item(key = "season_episodes") {
                        ShowDetailsSeasonEpisodesSection(
                            state = previewSeasonsEpisodesState,
                            status = previewHeaderState.status,
                            onAction = {},
                        )
                    }
                    item(key = "providers") {
                        ShowDetailsProvidersSection(state = previewProvidersState)
                    }
                    item(key = "trailers") {
                        ShowDetailsTrailersSection(state = previewTrailersState, onAction = {})
                    }
                    item(key = "casts") {
                        ShowDetailsCastSection(state = previewCastState)
                    }
                    item(key = "similar") {
                        ShowDetailsSimilarSection(state = previewSimilarState, onAction = {})
                    }
                    item(key = "bottom_spacer") {
                        Spacer(modifier = Modifier.height(54.dp))
                    }
                }
            }
        }
    }

    @Test
    fun showDetailsLoadedWithErrorInfoState() {
        composeTestRule.captureMultiDevice("ShowDetailsLoadedWithErrorInfoState") {
            TvManiacBackground {
                ShowDetailsScaffold(
                    hostState = previewHostStateWithMessage,
                    title = "",
                    isHeaderEmpty = true,
                    listState = LazyListState(),
                    onHostAction = {},
                    content = {},
                )
            }
        }
    }

    @Test
    fun showDetailsRatedState() {
        composeTestRule.captureMultiDevice("ShowDetailsRatedState") {
            TvManiacBackground {
                ShowDetailsScaffold(
                    hostState = previewHostState,
                    title = previewHeaderStateRated.title,
                    isHeaderEmpty = false,
                    listState = LazyListState(),
                    onHostAction = {},
                ) {
                    item(key = "header") {
                        ShowDetailsHeaderSection(state = previewHeaderStateRated, onAction = {})
                    }
                }
            }
        }
    }

    @Test
    fun showDetailsSimklProviderState() {
        composeTestRule.captureMultiDevice("ShowDetailsSimklProviderState") {
            TvManiacBackground {
                ShowDetailsScaffold(
                    hostState = previewHostState,
                    title = previewHeaderStateSimkl.title,
                    isHeaderEmpty = false,
                    listState = LazyListState(),
                    onHostAction = {},
                ) {
                    item(key = "header") {
                        ShowDetailsHeaderSection(state = previewHeaderStateSimkl, onAction = {})
                    }
                    item(key = "season_episodes") {
                        ShowDetailsSeasonEpisodesSection(
                            state = previewSeasonsEpisodesState,
                            status = previewHeaderStateSimkl.status,
                            onAction = {},
                        )
                    }
                    item(key = "providers") {
                        ShowDetailsProvidersSection(state = previewProvidersState)
                    }
                    item(key = "trailers") {
                        ShowDetailsTrailersSection(state = previewTrailersState, onAction = {})
                    }
                    item(key = "casts") {
                        ShowDetailsCastSection(state = previewCastState)
                    }
                    item(key = "similar") {
                        ShowDetailsSimilarSection(state = previewSimilarState, onAction = {})
                    }
                    item(key = "bottom_spacer") {
                        Spacer(modifier = Modifier.height(54.dp))
                    }
                }
            }
        }
    }
}
