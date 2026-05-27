package com.thomaskioko.tvmaniac.myshows.roborrazi

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import com.thomaskioko.tvmaniac.compose.components.TvManiacBackground
import com.thomaskioko.tvmaniac.myshows.presenter.MyShowsState
import com.thomaskioko.tvmaniac.myshows.ui.MyShowsScreen
import com.thomaskioko.tvmaniac.screenshottests.captureMultiDevice
import kotlinx.collections.immutable.persistentListOf
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
class MyShowsScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun myShowsPagerContinueWatchingTab() {
        composeTestRule.captureMultiDevice("MyShowsPagerContinueWatching") {
            TvManiacBackground {
                MyShowsScreen(
                    state = MyShowsState(
                        continueWatchingTitle = "Continue Watching",
                        startWatchingTitle = "Start Watching",
                    ),
                    tabs = persistentListOf("Continue Watching", "Start Watching"),
                    continueWatchingContent = { },
                    startWatchingContent = { },
                )
            }
        }
    }
}
