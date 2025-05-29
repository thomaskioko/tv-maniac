package com.thomaskioko.tvmaniac.ui.library

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.thomaskioko.tvmaniac.compose.components.TvManiacBackground
import com.thomaskioko.tvmaniac.presentation.watchlist.EmptyWatchlist
import com.thomaskioko.tvmaniac.presentation.watchlist.WatchlistContent
import com.thomaskioko.tvmaniac.screenshottests.captureMultiDevice
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
class LibraryScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun libraryScreenLoadedState() {
        composeTestRule.captureMultiDevice("LibraryContentState") {
            TvManiacBackground {
                WatchlistScreen(
                    state = WatchlistContent(list = list),
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
                    state = EmptyWatchlist(message = "Something went Wrong"),
                    onAction = {},
                )
            }
        }
    }
}
