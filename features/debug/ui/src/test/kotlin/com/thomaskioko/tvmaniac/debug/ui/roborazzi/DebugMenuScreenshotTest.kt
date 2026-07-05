package com.thomaskioko.tvmaniac.debug.ui.roborazzi

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import com.thomaskioko.tvmaniac.compose.components.TvManiacBackground
import com.thomaskioko.tvmaniac.debug.ui.AccountTypeDialog
import com.thomaskioko.tvmaniac.debug.ui.DebugMenuScreen
import com.thomaskioko.tvmaniac.debug.ui.accountTypeFreeState
import com.thomaskioko.tvmaniac.debug.ui.accountTypePremiumState
import com.thomaskioko.tvmaniac.debug.ui.defaultDebugState
import com.thomaskioko.tvmaniac.screenshottests.captureMultiDevice
import com.thomaskioko.tvmaniac.subscription.api.AccountType
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
class DebugMenuScreenshotTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun debugMenuScreenContent() {
        composeTestRule.captureMultiDevice("DebugMenuScreenContent") {
            TvManiacBackground {
                DebugMenuScreen(
                    state = defaultDebugState,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun debugMenuScreenAccountTypePremium() {
        composeTestRule.captureMultiDevice("DebugMenuScreenAccountTypePremium") {
            TvManiacBackground {
                DebugMenuScreen(
                    state = accountTypePremiumState,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun debugMenuScreenAccountTypeFree() {
        composeTestRule.captureMultiDevice("DebugMenuScreenAccountTypeFree") {
            TvManiacBackground {
                DebugMenuScreen(
                    state = accountTypeFreeState,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun accountTypeDialogOpen() {
        composeTestRule.captureMultiDevice("AccountTypeDialogOpen") {
            TvManiacBackground {
                AccountTypeDialog(
                    isVisible = true,
                    current = AccountType.None,
                    onOverrideSelected = {},
                    onDismiss = {},
                )
            }
        }
    }
}
