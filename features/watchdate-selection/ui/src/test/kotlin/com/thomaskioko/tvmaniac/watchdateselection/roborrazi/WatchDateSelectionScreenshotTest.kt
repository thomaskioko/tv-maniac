package com.thomaskioko.tvmaniac.watchdateselection.roborrazi

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import com.thomaskioko.tvmaniac.compose.components.TvManiacBackground
import com.thomaskioko.tvmaniac.screenshottests.captureMultiDevice
import com.thomaskioko.tvmaniac.watchdateselection.presenter.WatchDateSelectionState
import com.thomaskioko.tvmaniac.watchdateselection.ui.WatchDateSelectionContent
import kotlinx.datetime.LocalDate
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
class WatchDateSelectionScreenshotTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun watchDateSelectionDefault() {
        composeTestRule.captureMultiDevice("WatchDateSelectionDefault") {
            TvManiacBackground {
                WatchDateSelectionContent(state = state(), onAction = {})
            }
        }
    }

    @Test
    fun watchDateSelectionReleaseDateDisabled() {
        composeTestRule.captureMultiDevice("WatchDateSelectionReleaseDateDisabled") {
            TvManiacBackground {
                WatchDateSelectionContent(state = state(isReleaseDateEnabled = false), onAction = {})
            }
        }
    }

    @Test
    fun watchDateSelectionEditWithDate() {
        composeTestRule.captureMultiDevice("WatchDateSelectionEditWithDate") {
            TvManiacBackground {
                WatchDateSelectionContent(
                    state = state(
                        title = "Change watched date",
                        currentWatchedAtLabel = "12 Jan 2026 20:30",
                    ),
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun watchDateSelectionEditWithUnknownDate() {
        composeTestRule.captureMultiDevice("WatchDateSelectionEditWithUnknownDate") {
            TvManiacBackground {
                WatchDateSelectionContent(
                    state = state(
                        title = "Change watched date",
                        currentWatchedAtLabel = "A long time ago",
                    ),
                    onAction = {},
                )
            }
        }
    }

    private fun state(
        title: String = "When did you watch this?",
        isReleaseDateEnabled: Boolean = true,
        currentWatchedAtLabel: String? = null,
    ) = WatchDateSelectionState(
        title = title,
        justNowLabel = "Just now",
        releaseDateLabel = "Release date",
        otherDateLabel = "Other date…",
        unknownDateLabel = "Unknown date",
        isReleaseDateEnabled = isReleaseDateEnabled,
        currentWatchedAtLabel = currentWatchedAtLabel,
        maxSelectableDate = LocalDate(2026, 8, 16),
    )
}
