package com.thomaskioko.tvmaniac.compose.roborazzi

import androidx.activity.ComponentActivity
import androidx.compose.material3.Surface
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.thomaskioko.tvmaniac.compose.components.NotificationRationaleContent
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
internal class NotificationRationaleContentScreenshotTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun notificationRationaleContent() {
        composeTestRule.captureMultiDevice("NotificationRationaleContent") {
            Surface {
                NotificationRationaleContent(
                    onEnable = {},
                    onDismiss = {},
                )
            }
        }
    }
}
