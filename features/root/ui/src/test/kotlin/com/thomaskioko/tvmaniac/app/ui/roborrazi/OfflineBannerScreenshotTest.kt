package com.thomaskioko.tvmaniac.app.ui.roborrazi

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import com.thomaskioko.tvmaniac.app.ui.OfflineBanner
import com.thomaskioko.tvmaniac.presenter.root.model.ConnectivityBannerState
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
internal class OfflineBannerScreenshotTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun offlineBanner() {
        composeTestRule.captureMultiDevice("OfflineBanner") {
            Surface(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxSize()) {
                    OfflineBanner(
                        state = ConnectivityBannerState.Offline,
                        onDismiss = {},
                    )
                }
            }
        }
    }

    @Test
    fun backOnlineBanner() {
        composeTestRule.captureMultiDevice("BackOnlineBanner") {
            Surface(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxSize()) {
                    OfflineBanner(
                        state = ConnectivityBannerState.BackOnline,
                        onDismiss = {},
                    )
                }
            }
        }
    }
}
