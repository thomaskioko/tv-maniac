package com.thomaskioko.tvmaniac.watchlist.roborrazi

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.thomaskioko.tvmaniac.compose.components.TvManiacBackground
import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.screenshottests.captureMultiDevice
import com.thomaskioko.tvmaniac.ui.library.WatchlistScreen
import com.thomaskioko.tvmaniac.ui.library.watchlistItems
import com.thomaskioko.tvmaniac.watchlist.presenter.WatchlistState
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
class WatchlistScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun libraryScreenLoadedState() {
        composeTestRule.captureMultiDevice("LibraryContentState") {
            TvManiacBackground {
                WatchlistScreen(
                    state = WatchlistState(items = watchlistItems),
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun libraryScreenErrorLoadingShows() {
        composeTestRule.captureMultiDevice("ErrorLoadingShows") {
            TvManiacBackground {
                WatchlistScreen(
                    state = WatchlistState(
                        isGridMode = false,
                        items = watchlistItems,
                        message = UiMessage(message = "Something went Wrong"),
                    ),
                    onAction = {},
                )
            }
        }
    }
}
