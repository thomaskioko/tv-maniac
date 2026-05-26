package com.thomaskioko.tvmaniac.continuewatching.roborrazi

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import com.thomaskioko.tvmaniac.compose.components.TvManiacBackground
import com.thomaskioko.tvmaniac.continuewatching.presenter.ContinueWatchingState
import com.thomaskioko.tvmaniac.continuewatching.ui.ContinueWatchingScreen
import com.thomaskioko.tvmaniac.continuewatching.ui.continueWatchingItems
import com.thomaskioko.tvmaniac.continuewatching.ui.watchNextEpisodes
import com.thomaskioko.tvmaniac.screenshottests.captureMultiDevice
import kotlinx.collections.immutable.persistentListOf
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
class ContinueWatchingScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun continueWatchingListGridViewState() {
        composeTestRule.captureMultiDevice("ContinueWatchingListGridView") {
            TvManiacBackground {
                ContinueWatchingScreen(
                    state = ContinueWatchingState(
                        isRefreshing = false,
                        watchNextItems = continueWatchingItems,
                        staleItems = continueWatchingItems,
                    ),
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun continueWatchingListViewListView() {
        composeTestRule.captureMultiDevice("ContinueWatchingListView") {
            TvManiacBackground {
                ContinueWatchingScreen(
                    state = ContinueWatchingState(
                        isGridMode = false,
                        isRefreshing = false,
                        watchNextEpisodes = watchNextEpisodes,
                        staleEpisodes = watchNextEpisodes,
                    ),
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun continueWatchingScreenWatchNextOnlyState() {
        composeTestRule.captureMultiDevice("WatchNextOnly") {
            TvManiacBackground {
                ContinueWatchingScreen(
                    state = ContinueWatchingState(
                        isRefreshing = false,
                        watchNextItems = continueWatchingItems,
                        staleItems = persistentListOf(),
                    ),
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun continueWatchingScreenEmptyInProgressState() {
        composeTestRule.captureMultiDevice("EmptyInProgress") {
            TvManiacBackground {
                ContinueWatchingScreen(
                    state = ContinueWatchingState(
                        isRefreshing = false,
                        emptyStateText = "Nothing in progress yet. Mark an episode as watched to see it here.",
                        watchNextItems = persistentListOf(),
                        staleItems = persistentListOf(),
                    ),
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun continueWatchingScreenEmptyState() {
        composeTestRule.captureMultiDevice("EmptySearchResult") {
            TvManiacBackground {
                ContinueWatchingScreen(
                    state = ContinueWatchingState(
                        isGridMode = false,
                        isRefreshing = false,
                        emptyStateText = "No content",
                        watchNextItems = persistentListOf(),
                        query = "Show title",
                    ),
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun continueWatchingScreenErrorLoadingShows() {
        composeTestRule.captureMultiDevice("ErrorLoadingShows") {
            TvManiacBackground {
                ContinueWatchingScreen(
                    state = ContinueWatchingState(
                        isGridMode = false,
                        isRefreshing = false,
                        watchNextItems = persistentListOf(),
                    ),
                    onAction = {},
                )
            }
        }
    }
}
