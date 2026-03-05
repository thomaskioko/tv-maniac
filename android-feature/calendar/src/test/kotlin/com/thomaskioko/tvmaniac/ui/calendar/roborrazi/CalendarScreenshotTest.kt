package com.thomaskioko.tvmaniac.ui.calendar.roborrazi

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.thomaskioko.tvmaniac.compose.components.TvManiacBackground
import com.thomaskioko.tvmaniac.presentation.calendar.CalendarState
import com.thomaskioko.tvmaniac.presentation.calendar.model.CalendarDateGroup
import com.thomaskioko.tvmaniac.presentation.calendar.model.CalendarEpisodeItem
import com.thomaskioko.tvmaniac.screenshottests.captureMultiDevice
import com.thomaskioko.tvmaniac.ui.calendar.CalendarScreen
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
class CalendarScreenshotTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun calendarScreenLoadingState() {
        composeTestRule.captureMultiDevice("CalendarScreenLoadingState") {
            TvManiacBackground {
                CalendarScreen(
                    state = CalendarState(
                        isLoading = true,
                        isLoggedIn = true,
                        weekLabel = "Jan 31, 2026 - Feb 6, 2026",
                    ),
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun calendarScreenNotLoggedIn() {
        composeTestRule.captureMultiDevice("CalendarScreenNotLoggedIn") {
            TvManiacBackground {
                CalendarScreen(
                    state = CalendarState(
                        isLoading = false,
                        isLoggedIn = false,
                        weekLabel = "Jan 31, 2026 - Feb 6, 2026",
                        canNavigateNext = false,
                        loginTitle = "Nothing to see here",
                        loginMessage = "Login to Trakt to see your calendar",
                    ),
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun calendarScreenEmptyState() {
        composeTestRule.captureMultiDevice("CalendarScreenEmptyState") {
            TvManiacBackground {
                CalendarScreen(
                    state = CalendarState(
                        isLoading = false,
                        isLoggedIn = true,
                        weekLabel = "Jan 31, 2026 - Feb 6, 2026",
                        emptyTitle = "Nothing to see here",
                        emptyMessage = "No upcoming episodes",
                    ),
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun calendarScreenContentLoaded() {
        composeTestRule.captureMultiDevice("CalendarScreenContentLoaded") {
            TvManiacBackground {
                CalendarScreen(
                    state = CalendarState(
                        isLoading = false,
                        isLoggedIn = true,
                        weekLabel = "Jan 31, 2026 - Feb 6, 2026",
                        canNavigatePrevious = false,
                        moreEpisodesFormat = "+%d episodes",
                        dateGroups = persistentListOf(
                            CalendarDateGroup(
                                dateLabel = "Today, Jan 31, 2026",
                                episodes = persistentListOf(
                                    CalendarEpisodeItem(
                                        showTraktId = 1,
                                        episodeTraktId = 100,
                                        showTitle = "Severance",
                                        posterUrl = null,
                                        episodeInfo = "S02E01 · Hello, Ms. Cobel",
                                        airTime = "03:00",
                                        network = "Apple TV+",
                                        additionalEpisodesCount = 0,
                                        overview = "Mark leads the team on a new mission.",
                                        rating = 8.5,
                                        votes = 120,
                                        runtime = 50,
                                        formattedAirDate = "Friday, January 31, 2026 at 03:00",
                                    ),
                                ),
                            ),
                            CalendarDateGroup(
                                dateLabel = "Tomorrow, Feb 1, 2026",
                                episodes = persistentListOf(
                                    CalendarEpisodeItem(
                                        showTraktId = 2,
                                        episodeTraktId = 200,
                                        showTitle = "Hell's Paradise",
                                        posterUrl = null,
                                        episodeInfo = "S02E04 · The Battle Begins",
                                        airTime = "15:45",
                                        network = null,
                                        additionalEpisodesCount = 1,
                                        overview = null,
                                        rating = null,
                                        votes = null,
                                        runtime = 24,
                                        formattedAirDate = "Saturday, February 1, 2026 at 15:45",
                                    ),
                                ),
                            ),
                        ),
                    ),
                    onAction = {},
                )
            }
        }
    }
}
