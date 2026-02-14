package com.thomaskioko.tvmaniac.showdetails.roborrazi

import androidx.activity.ComponentActivity
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.thomaskioko.tvmaniac.compose.components.TvManiacBackground
import com.thomaskioko.tvmaniac.screenshottests.captureMultiDevice
import com.thomaskioko.tvmaniac.showdetails.ui.ShowDetailsScreen
import com.thomaskioko.tvmaniac.showdetails.ui.showDetailsContent
import com.thomaskioko.tvmaniac.showdetails.ui.showDetailsContentWithError
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
                ShowDetailsScreen(
                    state = showDetailsContent,
                    title = "",
                    listState = LazyListState(),
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun showDetailsLoadedWithErrorInfoState() {
        composeTestRule.captureMultiDevice("ShowDetailsLoadedWithErrorInfoState") {
            TvManiacBackground {
                ShowDetailsScreen(
                    state = showDetailsContentWithError,
                    title = "",
                    listState = LazyListState(),
                    onAction = {},
                )
            }
        }
    }
}
