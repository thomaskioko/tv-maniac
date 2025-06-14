package com.thomaskioko.tvmaniac.watchlist.roborrazi

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.thomaskioko.tvmaniac.compose.components.TvManiacBackground
import com.thomaskioko.tvmaniac.presenter.watchlist.EmptyWatchlist
import com.thomaskioko.tvmaniac.presenter.watchlist.WatchlistContent
import com.thomaskioko.tvmaniac.screenshottests.captureMultiDevice
import com.thomaskioko.tvmaniac.ui.library.WatchlistScreen
import com.thomaskioko.tvmaniac.ui.library.list
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
