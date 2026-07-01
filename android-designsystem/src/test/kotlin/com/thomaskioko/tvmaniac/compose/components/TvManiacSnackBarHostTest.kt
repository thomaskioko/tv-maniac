package com.thomaskioko.tvmaniac.compose.components

import androidx.activity.ComponentActivity
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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
internal class TvManiacSnackBarHostTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun `should keep dismissed content visible while exit animation runs`() {
        var message by mutableStateOf<String?>("Connection lost")
        var style by mutableStateOf(SnackBarStyle.Error)
        var loading by mutableStateOf(false)

        composeTestRule.setContent {
            Surface {
                TvManiacSnackBarHost(
                    message = message,
                    style = style,
                    persistent = true,
                    loading = loading,
                    onDismiss = {
                        message = null
                        style = SnackBarStyle.Info
                        loading = true
                    },
                )
            }
        }

        composeTestRule.onNodeWithText("Connection lost").assertIsDisplayed()

        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.onNodeWithContentDescription("Dismiss").performClick()
        composeTestRule.mainClock.advanceTimeBy(100)

        composeTestRule.onNodeWithText("Connection lost").assertExists()
    }
}
