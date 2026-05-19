package com.thomaskioko.tvmaniac.featureflags.ui.roborazzi

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import com.thomaskioko.tvmaniac.compose.components.TvManiacBackground
import com.thomaskioko.tvmaniac.featureflags.ui.FeatureFlagsScreen
import com.thomaskioko.tvmaniac.featureflags.ui.defaultFeatureFlagsState
import com.thomaskioko.tvmaniac.featureflags.ui.emptyFeatureFlagsState
import com.thomaskioko.tvmaniac.featureflags.ui.localSourceFeatureFlagsState
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
class FeatureFlagsScreenshotTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun featureFlagsScreenDefaultState() {
        composeTestRule.captureMultiDevice("FeatureFlagsScreenDefaultState") {
            TvManiacBackground {
                FeatureFlagsScreen(
                    state = defaultFeatureFlagsState,
                    onBackClicked = {},
                    onSearchQueryChanged = {},
                    onResetAll = {},
                    onForceRefresh = {},
                    onToggleFlag = { _, _ -> },
                    onResetFlag = {},
                    onSortChanged = {},
                    onDirectionToggled = {},
                    onGroupByTypeToggled = {},
                )
            }
        }
    }

    @Test
    fun featureFlagsScreenLocalSourceState() {
        composeTestRule.captureMultiDevice("FeatureFlagsScreenLocalSourceState") {
            TvManiacBackground {
                FeatureFlagsScreen(
                    state = localSourceFeatureFlagsState,
                    onBackClicked = {},
                    onSearchQueryChanged = {},
                    onResetAll = {},
                    onForceRefresh = {},
                    onToggleFlag = { _, _ -> },
                    onResetFlag = {},
                    onSortChanged = {},
                    onDirectionToggled = {},
                    onGroupByTypeToggled = {},
                )
            }
        }
    }

    @Test
    fun featureFlagsScreenEmptyState() {
        composeTestRule.captureMultiDevice("FeatureFlagsScreenEmptyState") {
            TvManiacBackground {
                FeatureFlagsScreen(
                    state = emptyFeatureFlagsState,
                    onBackClicked = {},
                    onSearchQueryChanged = {},
                    onResetAll = {},
                    onForceRefresh = {},
                    onToggleFlag = { _, _ -> },
                    onResetFlag = {},
                    onSortChanged = {},
                    onDirectionToggled = {},
                    onGroupByTypeToggled = {},
                )
            }
        }
    }
}
