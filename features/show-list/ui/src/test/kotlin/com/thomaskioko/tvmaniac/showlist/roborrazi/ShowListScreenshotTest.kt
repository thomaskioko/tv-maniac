package com.thomaskioko.tvmaniac.showlist.roborrazi

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import com.thomaskioko.tvmaniac.screenshottests.captureMultiDevice
import com.thomaskioko.tvmaniac.showlist.ui.ShowListContent
import com.thomaskioko.tvmaniac.showlist.ui.emptyState
import com.thomaskioko.tvmaniac.showlist.ui.withCreateField
import com.thomaskioko.tvmaniac.showlist.ui.withCreateLoading
import com.thomaskioko.tvmaniac.showlist.ui.withLists
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
    fun showListEmpty() {
        composeTestRule.captureMultiDevice("ShowListEmpty") {
            Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
                ShowListContent(
                    state = emptyState,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun showListWithLists() {
        composeTestRule.captureMultiDevice("ShowListWithLists") {
            Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
                ShowListContent(
                    state = withLists,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun showListWithCreateField() {
        composeTestRule.captureMultiDevice("ShowListWithCreateField") {
            Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
                ShowListContent(
                    state = withCreateField,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun showListCreatingList() {
        composeTestRule.captureMultiDevice("ShowListCreatingList") {
            Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
                ShowListContent(
                    state = withCreateLoading,
                    onAction = {},
                )
            }
        }
    }
}
