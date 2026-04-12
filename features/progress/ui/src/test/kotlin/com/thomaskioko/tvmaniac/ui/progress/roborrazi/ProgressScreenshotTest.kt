package com.thomaskioko.tvmaniac.ui.progress.roborrazi

import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.thomaskioko.tvmaniac.compose.components.TvManiacBackground
import com.thomaskioko.tvmaniac.screenshottests.captureMultiDevice
import com.thomaskioko.tvmaniac.ui.progress.ProgressScreen
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
class ProgressScreenshotTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun progressScreenUpNextTab() {
        composeTestRule.captureMultiDevice("ProgressScreenUpNextTab") {
            TvManiacBackground {
                ProgressScreen(
                    selectedPage = 0,
                    isLoading = false,
                    tabs = listOf("Up Next", "Calendar"),
                    upNextContent = { Text("Up Next Content") },
                    calendarContent = { Text("Calendar Content") },
                )
            }
        }
    }

    @Test
    fun progressScreenCalendarTab() {
        composeTestRule.captureMultiDevice("ProgressScreenCalendarTab") {
            TvManiacBackground {
                ProgressScreen(
                    selectedPage = 1,
                    isLoading = false,
                    tabs = listOf("Up Next", "Calendar"),
                    upNextContent = { Text("Up Next Content") },
                    calendarContent = { Text("Calendar Content") },
                )
            }
        }
    }

    @Test
    fun progressScreenLoading() {
        composeTestRule.captureMultiDevice("ProgressScreenLoading") {
            TvManiacBackground {
                ProgressScreen(
                    selectedPage = 0,
                    isLoading = true,
                    tabs = listOf("Up Next", "Calendar"),
                    upNextContent = { Text("Up Next Content") },
                    calendarContent = { Text("Calendar Content") },
                )
            }
        }
    }
}
