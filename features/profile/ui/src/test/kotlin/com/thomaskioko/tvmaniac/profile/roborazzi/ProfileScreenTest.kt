package com.thomaskioko.tvmaniac.profile.roborazzi

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import com.thomaskioko.tvmaniac.compose.components.TvManiacBackground
import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileListItem
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileState
import com.thomaskioko.tvmaniac.profile.presenter.model.SectionState
import com.thomaskioko.tvmaniac.profile.ui.ProfileScreen
import com.thomaskioko.tvmaniac.profile.ui.authenticatedState
import com.thomaskioko.tvmaniac.profile.ui.sampleProfileLabels
import com.thomaskioko.tvmaniac.profile.ui.unauthenticatedState
import com.thomaskioko.tvmaniac.screenshottests.captureMultiDevice
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
internal class ProfileScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun profileScreenUnauthenticatedState() {
        composeTestRule.captureMultiDevice("ProfileScreenUnauthenticatedState") {
            TvManiacBackground {
                ProfileScreen(
                    state = unauthenticatedState,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun profileScreenLoadingState() {
        composeTestRule.captureMultiDevice("ProfileScreenLoadingState") {
            TvManiacBackground {
                ProfileScreen(
                    state = ProfileState(
                        isLoading = true,
                        userProfile = null,
                        errorMessage = null,
                        authenticated = false,
                        labels = sampleProfileLabels,
                    ),
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun profileScreenAuthenticatedState() {
        composeTestRule.captureMultiDevice("ProfileScreenAuthenticatedState") {
            TvManiacBackground {
                ProfileScreen(
                    state = authenticatedState,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun profileScreenUserListsWithMoreState() {
        val lists = (1..5).map { index ->
            ProfileListItem(
                id = index.toLong(),
                name = "List $index",
                itemCount = index,
                itemCountLabel = "$index shows",
                posterUrls = persistentListOf("/a.jpg", "/b.jpg", "/c.jpg", "/d.jpg"),
            )
        }
        composeTestRule.captureMultiDevice("ProfileScreenUserListsWithMoreState") {
            TvManiacBackground {
                ProfileScreen(
                    state = authenticatedState.copy(
                        userLists = SectionState.Content(persistentListOf(*lists.toTypedArray())),
                    ),
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun profileScreenUserListsEmptyState() {
        composeTestRule.captureMultiDevice("ProfileScreenUserListsEmptyState") {
            TvManiacBackground {
                ProfileScreen(
                    state = authenticatedState.copy(userLists = SectionState.Empty),
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun profileScreenUserListsErrorState() {
        composeTestRule.captureMultiDevice("ProfileScreenUserListsErrorState") {
            TvManiacBackground {
                ProfileScreen(
                    state = authenticatedState.copy(
                        userLists = SectionState.Error(UiMessage(message = "Failed to load lists")),
                    ),
                    onAction = {},
                )
            }
        }
    }
}
