package com.thomaskioko.tvmaniac.seasondetails.roborrazi

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.thomaskioko.tvmaniac.compose.components.TvManiacBackground
import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.screenshottests.captureMultiDevice
import com.thomaskioko.tvmaniac.seasondetails.ui.SeasonDetailsScreen
import com.thomaskioko.tvmaniac.seasondetails.ui.seasonDetailsLoaded
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
class SeasonScreenshotTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun seasonDetailsScreenContentLoadedState() {
        composeTestRule.captureMultiDevice("SeasonDetailsLoadedContent") {
            TvManiacBackground {
                SeasonDetailsScreen(
                    state = seasonDetailsLoaded,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun seasonDetailsScreenErrorState() {
        composeTestRule.captureMultiDevice("SeasonDetailsErrorState") {
            TvManiacBackground {
                SeasonDetailsScreen(
                    state = seasonDetailsLoaded.copy(message = UiMessage("Opps! Something went wrong")),
                    onAction = {},
                )
            }
        }
    }
}
