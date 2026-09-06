package com.thomaskioko.tvmaniac.lists.roborazzi

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import com.thomaskioko.tvmaniac.compose.components.TvManiacBackground
import com.thomaskioko.tvmaniac.lists.ui.ListsScreen
import com.thomaskioko.tvmaniac.lists.ui.contentState
import com.thomaskioko.tvmaniac.lists.ui.emptyState
import com.thomaskioko.tvmaniac.lists.ui.errorState
import com.thomaskioko.tvmaniac.lists.ui.loadingState
import com.thomaskioko.tvmaniac.screenshottests.captureMultiDevice
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
internal class ListsScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun listsScreenLoadingState() {
        composeTestRule.captureMultiDevice("ListsScreenLoadingState") {
            TvManiacBackground {
                ListsScreen(
                    state = loadingState,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun listsScreenEmptyState() {
        composeTestRule.captureMultiDevice("ListsScreenEmptyState") {
            TvManiacBackground {
                ListsScreen(
                    state = emptyState,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun listsScreenContentState() {
        composeTestRule.captureMultiDevice("ListsScreenContentState") {
            TvManiacBackground {
                ListsScreen(
                    state = contentState,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun listsScreenErrorState() {
        composeTestRule.captureMultiDevice("ListsScreenErrorState") {
            TvManiacBackground {
                ListsScreen(
                    state = errorState,
                    onAction = {},
                )
            }
        }
    }
}
