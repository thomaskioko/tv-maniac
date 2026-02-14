package com.thomaskioko.tvmaniac.compose.roborazzi

import androidx.activity.ComponentActivity
import androidx.compose.material3.Surface
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.thomaskioko.tvmaniac.compose.components.SnackBarStyle
import com.thomaskioko.tvmaniac.compose.components.TvManiacSnackBar
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
internal class TvManiacSnackBarScreenshotTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun snackBarError() {
        composeTestRule.captureMultiDevice("TvManiacSnackBar_Error") {
            Surface {
                TvManiacSnackBar(
                    message = "Something went wrong while syncing your data. Check your internet connection.",
                    style = SnackBarStyle.Error,
                )
            }
        }
    }

    @Test
    fun snackBarWarning() {
        composeTestRule.captureMultiDevice("TvManiacSnackBar_Warning") {
            Surface {
                TvManiacSnackBar(
                    message = "Your session is about to expire.",
                    style = SnackBarStyle.Warning,
                )
            }
        }
    }

    @Test
    fun snackBarSuccess() {
        composeTestRule.captureMultiDevice("TvManiacSnackBar_Success") {
            Surface {
                TvManiacSnackBar(
                    message = "Changes saved successfully.",
                    style = SnackBarStyle.Success,
                )
            }
        }
    }

    @Test
    fun snackBarInfo() {
        composeTestRule.captureMultiDevice("TvManiacSnackBar_Info") {
            Surface {
                TvManiacSnackBar(
                    message = "Your data has been synced successfully.",
                    style = SnackBarStyle.Info,
                )
            }
        }
    }
}
