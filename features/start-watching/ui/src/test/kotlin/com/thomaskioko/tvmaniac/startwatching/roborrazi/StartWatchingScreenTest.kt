package com.thomaskioko.tvmaniac.startwatching.roborrazi

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import com.thomaskioko.tvmaniac.compose.components.TvManiacBackground
import com.thomaskioko.tvmaniac.screenshottests.captureMultiDevice
import com.thomaskioko.tvmaniac.startwatching.presenter.StartWatchingState
import com.thomaskioko.tvmaniac.startwatching.ui.StartWatchingScreen
import com.thomaskioko.tvmaniac.startwatching.ui.previewStartWatchingItems
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
class StartWatchingScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun startWatchingContentState() {
        composeTestRule.captureMultiDevice("StartWatchingContent") {
            TvManiacBackground {
                StartWatchingScreen(
                    state = StartWatchingState(
                        items = previewStartWatchingItems,
                    ),
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun startWatchingListState() {
        composeTestRule.captureMultiDevice("StartWatchingList") {
            TvManiacBackground {
                StartWatchingScreen(
                    state = StartWatchingState(
                        isGridMode = false,
                        items = previewStartWatchingItems,
                    ),
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun startWatchingEmptyState() {
        composeTestRule.captureMultiDevice("StartWatchingEmpty") {
            TvManiacBackground {
                StartWatchingScreen(
                    state = StartWatchingState(),
                    onAction = {},
                )
            }
        }
    }
}
