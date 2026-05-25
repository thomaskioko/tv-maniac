package com.thomaskioko.tvmaniac.myshows.roborrazi

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import com.thomaskioko.tvmaniac.compose.components.TvManiacBackground
import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.myshows.presenter.MyShowsState
import com.thomaskioko.tvmaniac.myshows.ui.MyShowsScreen
import com.thomaskioko.tvmaniac.myshows.ui.myShowsItems
import com.thomaskioko.tvmaniac.myshows.ui.watchNextEpisodes
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
class MyShowsScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun myShowsListGridViewState() {
        composeTestRule.captureMultiDevice("MyShowsListGridView") {
            TvManiacBackground {
                MyShowsScreen(
                    state = MyShowsState(
                        isRefreshing = false,
                        watchNextItems = myShowsItems,
                        staleItems = myShowsItems,
                    ),
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun myShowsListViewListView() {
        composeTestRule.captureMultiDevice("MyShowsListView") {
            TvManiacBackground {
                MyShowsScreen(
                    state = MyShowsState(
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
    fun myShowsScreenSearchActiveState() {
        composeTestRule.captureMultiDevice("SearchActiveResult") {
            TvManiacBackground {
                MyShowsScreen(
                    state = MyShowsState(
                        isGridMode = false,
                        isRefreshing = false,
                        isSearchActive = true,
                        watchNextEpisodes = watchNextEpisodes,
                        staleEpisodes = watchNextEpisodes,
                    ),
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun myShowsScreenWatchNextOnlyState() {
        composeTestRule.captureMultiDevice("WatchNextOnly") {
            TvManiacBackground {
                MyShowsScreen(
                    state = MyShowsState(
                        isRefreshing = false,
                        watchNextItems = myShowsItems,
                        staleItems = persistentListOf(),
                    ),
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun myShowsScreenEmptyInProgressState() {
        composeTestRule.captureMultiDevice("EmptyInProgress") {
            TvManiacBackground {
                MyShowsScreen(
                    state = MyShowsState(
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
    fun myShowsScreenEmptyState() {
        composeTestRule.captureMultiDevice("EmptySearchResult") {
            TvManiacBackground {
                MyShowsScreen(
                    state = MyShowsState(
                        isGridMode = false,
                        isRefreshing = false,
                        emptyStateText = "No content",
                        watchNextItems = persistentListOf(),
                        query = "Show title",
                        message = UiMessage(message = "Something went Wrong"),
                    ),
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun myShowsScreenErrorLoadingShows() {
        composeTestRule.captureMultiDevice("ErrorLoadingShows") {
            TvManiacBackground {
                MyShowsScreen(
                    state = MyShowsState(
                        isGridMode = false,
                        isRefreshing = false,
                        watchNextItems = persistentListOf(),
                        message = UiMessage(message = "Something went Wrong"),
                    ),
                    onAction = {},
                )
            }
        }
    }
}
