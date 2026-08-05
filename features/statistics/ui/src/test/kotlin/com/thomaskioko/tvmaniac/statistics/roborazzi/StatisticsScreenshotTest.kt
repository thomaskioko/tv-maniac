package com.thomaskioko.tvmaniac.statistics.roborazzi

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import com.thomaskioko.tvmaniac.compose.components.TvManiacBackground
import com.thomaskioko.tvmaniac.screenshottests.captureMultiDevice
import com.thomaskioko.tvmaniac.statistics.ui.StatisticsScreen
import com.thomaskioko.tvmaniac.statistics.ui.contentState
import com.thomaskioko.tvmaniac.statistics.ui.emptyState
import com.thomaskioko.tvmaniac.statistics.ui.loadingState
import com.thomaskioko.tvmaniac.statistics.ui.lockedState
import com.thomaskioko.tvmaniac.statistics.ui.markedWatchedTimesState
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
internal class StatisticsScreenshotTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun statisticsScreenLoadingState() {
        composeTestRule.captureMultiDevice("StatisticsScreenLoadingState") {
            TvManiacBackground {
                StatisticsScreen(
                    state = loadingState,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun statisticsScreenEmptyState() {
        composeTestRule.captureMultiDevice("StatisticsScreenEmptyState") {
            TvManiacBackground {
                StatisticsScreen(
                    state = emptyState,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun statisticsScreenContentState() {
        composeTestRule.captureMultiDevice("StatisticsScreenContentState") {
            TvManiacBackground {
                StatisticsScreen(
                    state = contentState,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun statisticsScreenMarkedWatchedTimesState() {
        composeTestRule.captureMultiDevice("StatisticsScreenMarkedWatchedTimesState") {
            TvManiacBackground {
                StatisticsScreen(
                    state = markedWatchedTimesState,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun statisticsScreenLockedState() {
        composeTestRule.captureMultiDevice("StatisticsScreenLockedState") {
            TvManiacBackground {
                StatisticsScreen(
                    state = lockedState,
                    onAction = {},
                )
            }
        }
    }
}
