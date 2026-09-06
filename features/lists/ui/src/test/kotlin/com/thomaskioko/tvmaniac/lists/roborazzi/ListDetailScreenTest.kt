package com.thomaskioko.tvmaniac.lists.roborazzi

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import com.thomaskioko.tvmaniac.compose.components.TvManiacBackground
import com.thomaskioko.tvmaniac.lists.ui.ListDetailScreen
import com.thomaskioko.tvmaniac.lists.ui.listDetailContentState
import com.thomaskioko.tvmaniac.lists.ui.listDetailEmptyState
import com.thomaskioko.tvmaniac.lists.ui.listDetailErrorState
import com.thomaskioko.tvmaniac.lists.ui.listDetailLoadMoreErrorState
import com.thomaskioko.tvmaniac.lists.ui.listDetailLoadingState
import com.thomaskioko.tvmaniac.lists.ui.listDetailRemoveConfirmationState
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
internal class ListDetailScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun listDetailScreenLoadingState() {
        composeTestRule.captureMultiDevice("ListDetailScreenLoadingState") {
            TvManiacBackground {
                ListDetailScreen(
                    state = listDetailLoadingState,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun listDetailScreenEmptyState() {
        composeTestRule.captureMultiDevice("ListDetailScreenEmptyState") {
            TvManiacBackground {
                ListDetailScreen(
                    state = listDetailEmptyState,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun listDetailScreenContentState() {
        composeTestRule.captureMultiDevice("ListDetailScreenContentState") {
            TvManiacBackground {
                ListDetailScreen(
                    state = listDetailContentState,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun listDetailScreenErrorState() {
        composeTestRule.captureMultiDevice("ListDetailScreenErrorState") {
            TvManiacBackground {
                ListDetailScreen(
                    state = listDetailErrorState,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun listDetailScreenLoadMoreErrorState() {
        composeTestRule.captureMultiDevice("ListDetailScreenLoadMoreErrorState") {
            TvManiacBackground {
                ListDetailScreen(
                    state = listDetailLoadMoreErrorState,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun listDetailScreenRemoveConfirmationDialog() {
        composeTestRule.captureMultiDevice("ListDetailScreenRemoveConfirmationDialog") {
            TvManiacBackground {
                ListDetailScreen(
                    state = listDetailRemoveConfirmationState,
                    onAction = {},
                )
            }
        }
    }
}
