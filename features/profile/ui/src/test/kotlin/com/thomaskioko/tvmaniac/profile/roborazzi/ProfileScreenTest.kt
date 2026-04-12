package com.thomaskioko.tvmaniac.profile.roborazzi

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.thomaskioko.tvmaniac.compose.components.TvManiacBackground
import com.thomaskioko.tvmaniac.profile.ui.ProfileScreen
import com.thomaskioko.tvmaniac.profile.ui.authenticatedState
import com.thomaskioko.tvmaniac.profile.ui.unauthenticatedState
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
internal class ProfileScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun profileScreenUnauthenticatedState() {
        composeTestRule.captureMultiDevice("ProfileScreenUnauthenticatedState") {
            TvManiacBackground {
                ProfileScreen(
                    state = unauthenticatedState,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun profileScreenAuthenticatedState() {
        composeTestRule.captureMultiDevice("ProfileScreenAuthenticatedState") {
            TvManiacBackground {
                ProfileScreen(
                    state = authenticatedState,
                    onAction = {},
                )
            }
        }
    }
}
