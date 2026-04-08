package com.thomaskioko.tvmaniac.showdetails.roborrazi

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.thomaskioko.tvmaniac.compose.components.TvManiacBackground
import com.thomaskioko.tvmaniac.screenshottests.captureMultiDevice
import com.thomaskioko.tvmaniac.showdetails.ui.ShowListSheetContent
import com.thomaskioko.tvmaniac.showdetails.ui.showDetailsWithCreateFieldExpanded
import com.thomaskioko.tvmaniac.showdetails.ui.showDetailsWithCreateListLoading
import com.thomaskioko.tvmaniac.showdetails.ui.showDetailsWithEmptyTraktLists
import com.thomaskioko.tvmaniac.showdetails.ui.showDetailsWithTraktLists
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
class ShowListSheetScreenshotTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun showListSheetWithLists() {
        composeTestRule.captureMultiDevice("ShowListSheetWithLists") {
            TvManiacBackground {
                ShowListSheetContent(
                    state = showDetailsWithTraktLists,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun showListSheetWithCreateField() {
        composeTestRule.captureMultiDevice("ShowListSheetWithCreateField") {
            TvManiacBackground {
                ShowListSheetContent(
                    state = showDetailsWithCreateFieldExpanded,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun showListSheetCreatingList() {
        composeTestRule.captureMultiDevice("ShowListSheetCreatingList") {
            TvManiacBackground {
                ShowListSheetContent(
                    state = showDetailsWithCreateListLoading,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun showListSheetEmpty() {
        composeTestRule.captureMultiDevice("ShowListSheetEmpty") {
            TvManiacBackground {
                ShowListSheetContent(
                    state = showDetailsWithEmptyTraktLists,
                    onAction = {},
                )
            }
        }
    }
}
