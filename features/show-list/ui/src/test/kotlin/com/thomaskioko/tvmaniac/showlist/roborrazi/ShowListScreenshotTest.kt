package com.thomaskioko.tvmaniac.showlist.roborrazi

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import com.thomaskioko.tvmaniac.compose.components.TvManiacBackground
import com.thomaskioko.tvmaniac.screenshottests.captureMultiDevice
import com.thomaskioko.tvmaniac.showlist.ui.ShowListContent
import com.thomaskioko.tvmaniac.showlist.ui.loggedInEmpty
import com.thomaskioko.tvmaniac.showlist.ui.loggedInWithCreateField
import com.thomaskioko.tvmaniac.showlist.ui.loggedInWithCreateLoading
import com.thomaskioko.tvmaniac.showlist.ui.loggedInWithLists
import com.thomaskioko.tvmaniac.showlist.ui.loggedOutState
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
class ShowListScreenshotTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun showListLoggedOut() {
        composeTestRule.captureMultiDevice("ShowListLoggedOut") {
            TvManiacBackground {
                ShowListContent(
                    state = loggedOutState,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun showListEmpty() {
        composeTestRule.captureMultiDevice("ShowListEmpty") {
            TvManiacBackground {
                ShowListContent(
                    state = loggedInEmpty,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun showListWithLists() {
        composeTestRule.captureMultiDevice("ShowListWithLists") {
            TvManiacBackground {
                ShowListContent(
                    state = loggedInWithLists,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun showListWithCreateField() {
        composeTestRule.captureMultiDevice("ShowListWithCreateField") {
            TvManiacBackground {
                ShowListContent(
                    state = loggedInWithCreateField,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun showListCreatingList() {
        composeTestRule.captureMultiDevice("ShowListCreatingList") {
            TvManiacBackground {
                ShowListContent(
                    state = loggedInWithCreateLoading,
                    onAction = {},
                )
            }
        }
    }
}
