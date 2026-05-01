package com.thomaskioko.tvmaniac.ui.progress.roborrazi

import androidx.activity.ComponentActivity
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import com.thomaskioko.tvmaniac.compose.components.TvManiacBackground
import com.thomaskioko.tvmaniac.presentation.calendar.CalendarState
import com.thomaskioko.tvmaniac.presentation.progress.ProgressState
import com.thomaskioko.tvmaniac.presentation.upnext.UpNextState
import com.thomaskioko.tvmaniac.screenshottests.captureMultiDevice
import com.thomaskioko.tvmaniac.ui.progress.ProgressScreen
import com.thomaskioko.tvmaniac.ui.progress.previewCalendarEvents
import com.thomaskioko.tvmaniac.ui.progress.previewUpNextEpisodes
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
                    progressState = ProgressState(selectedPage = 0),
                    upNextState = UpNextState(
                        isLoading = false,
                        episodes = previewUpNextEpisodes(),
                    ),
                    calendarState = CalendarState(
                        isLoading = false,
                        isLoggedIn = true,
                        weekLabel = "Jan 31, 2026 - Feb 6, 2026",
                        dateGroups = previewCalendarEvents(),
                    ),
                    tabs = listOf("Up Next", "Calendar"),
                    modifier = Modifier,
                    progressAction = {},
                    upNextAction = {},
                    calendarAction = {},
                )
            }
        }
    }

    @Test
    fun progressScreenCalendarTab() {
        composeTestRule.captureMultiDevice("ProgressScreenCalendarTab") {
            TvManiacBackground {
                ProgressScreen(
                    progressState = ProgressState(selectedPage = 1),
                    upNextState = UpNextState(
                        isLoading = false,
                        episodes = previewUpNextEpisodes(),
                    ),
                    calendarState = CalendarState(
                        isLoading = false,
                        isLoggedIn = true,
                        weekLabel = "Jan 31, 2026 - Feb 6, 2026",
                        dateGroups = previewCalendarEvents(),
                    ),
                    tabs = listOf("Up Next", "Calendar"),
                    modifier = Modifier,
                    progressAction = {},
                    upNextAction = {},
                    calendarAction = {},
                )
            }
        }
    }

    @Test
    fun progressScreenLoading() {
        composeTestRule.captureMultiDevice("ProgressScreenLoading") {
            TvManiacBackground {
                ProgressScreen(
                    progressState = ProgressState(selectedPage = 0),
                    upNextState = UpNextState(
                        isLoading = true,
                        episodes = previewUpNextEpisodes(),
                    ),
                    calendarState = CalendarState(
                        isLoading = false,
                        isLoggedIn = true,
                        weekLabel = "Jan 31, 2026 - Feb 6, 2026",
                        dateGroups = previewCalendarEvents(),
                    ),
                    tabs = listOf("Up Next", "Calendar"),
                    modifier = Modifier,
                    progressAction = {},
                    upNextAction = {},
                    calendarAction = {},
                )
            }
        }
    }
}
