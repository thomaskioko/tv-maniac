package com.thomaskioko.tvmaniac.compose.roborazzi

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import com.thomaskioko.tvmaniac.compose.components.BannerStyle
import com.thomaskioko.tvmaniac.compose.components.TvManiacBanner
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
internal class TvManiacBannerScreenshotTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun bannerError() {
        composeTestRule.captureMultiDevice("TvManiacBanner_Error") {
            ScreenScaffold {
                TvManiacBanner(
                    message = "Your Trakt account is full. Upgrade to keep syncing new shows.",
                    onDismiss = {},
                    style = BannerStyle.Error,
                    dismissContentDescription = "Dismiss",
                )
            }
        }
    }

    @Test
    fun bannerWarning() {
        composeTestRule.captureMultiDevice("TvManiacBanner_Warning") {
            ScreenScaffold {
                TvManiacBanner(
                    message = "Your session is about to expire.",
                    onDismiss = {},
                    style = BannerStyle.Warning,
                    dismissContentDescription = "Dismiss",
                )
            }
        }
    }

    @Test
    fun bannerSuccess() {
        composeTestRule.captureMultiDevice("TvManiacBanner_Success") {
            ScreenScaffold {
                TvManiacBanner(
                    message = "Library synced successfully.",
                    onDismiss = {},
                    style = BannerStyle.Success,
                    dismissContentDescription = "Dismiss",
                )
            }
        }
    }

    @Test
    fun bannerInfo() {
        composeTestRule.captureMultiDevice("TvManiacBanner_Info") {
            ScreenScaffold {
                TvManiacBanner(
                    message = "New episode notifications are now available.",
                    onDismiss = {},
                    style = BannerStyle.Info,
                    dismissContentDescription = "Dismiss",
                )
            }
        }
    }

    @Test
    fun bannerWithAction() {
        composeTestRule.captureMultiDevice("TvManiacBanner_WithAction") {
            ScreenScaffold {
                TvManiacBanner(
                    message = "Your Trakt account is full. Upgrade to keep syncing new shows.",
                    onDismiss = {},
                    style = BannerStyle.Error,
                    dismissContentDescription = "Dismiss",
                    action = {
                        Button(
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LocalContentColor.current,
                                contentColor = MaterialTheme.colorScheme.errorContainer,
                            ),
                            shape = RoundedCornerShape(20),
                        ) {
                            Text("Upgrade")
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun ScreenScaffold(content: @Composable () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            content()
        }
    }
}
