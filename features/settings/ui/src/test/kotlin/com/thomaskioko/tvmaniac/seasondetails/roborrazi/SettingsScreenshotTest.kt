package com.thomaskioko.tvmaniac.seasondetails.roborrazi

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import com.thomaskioko.tvmaniac.compose.components.TvManiacBackground
import com.thomaskioko.tvmaniac.screenshottests.captureMultiDevice
import com.thomaskioko.tvmaniac.settings.ui.SettingsScreen
import com.thomaskioko.tvmaniac.settings.ui.accountLoggedOutState
import com.thomaskioko.tvmaniac.settings.ui.accountLoggingOutState
import com.thomaskioko.tvmaniac.settings.ui.accountState
import com.thomaskioko.tvmaniac.settings.ui.accountSwitchDialogState
import com.thomaskioko.tvmaniac.settings.ui.accountSwitchState
import com.thomaskioko.tvmaniac.settings.ui.accountSwitchingState
import com.thomaskioko.tvmaniac.settings.ui.appearanceLockedState
import com.thomaskioko.tvmaniac.settings.ui.appearanceState
import com.thomaskioko.tvmaniac.settings.ui.behaviorState
import com.thomaskioko.tvmaniac.settings.ui.defaultState
import com.thomaskioko.tvmaniac.settings.ui.infoState
import com.thomaskioko.tvmaniac.settings.ui.layoutState
import com.thomaskioko.tvmaniac.settings.ui.licensesState
import com.thomaskioko.tvmaniac.settings.ui.loadingState
import com.thomaskioko.tvmaniac.settings.ui.loggedInState
import com.thomaskioko.tvmaniac.settings.ui.notificationsLockedState
import com.thomaskioko.tvmaniac.settings.ui.notificationsState
import com.thomaskioko.tvmaniac.settings.ui.privacyState
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
class SettingsScreenshotTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun settingsScreenLoadingState() {
        composeTestRule.captureMultiDevice("SettingsScreenLoadingState") {
            TvManiacBackground {
                SettingsScreen(
                    state = loadingState,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun settingsScreenDefaultState() {
        composeTestRule.captureMultiDevice("SettingsScreenDefaultState") {
            TvManiacBackground {
                SettingsScreen(
                    state = defaultState,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun settingsScreenLogInState() {
        composeTestRule.captureMultiDevice("SettingsScreenLogInState") {
            TvManiacBackground {
                SettingsScreen(
                    state = loggedInState,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun settingsScreenLayoutPage() {
        composeTestRule.captureMultiDevice("SettingsScreenLayoutPage") {
            TvManiacBackground {
                SettingsScreen(
                    state = layoutState,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun settingsScreenAppearancePage() {
        composeTestRule.captureMultiDevice("SettingsScreenAppearancePage") {
            TvManiacBackground {
                SettingsScreen(
                    state = appearanceState,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun settingsScreenAppearancePageLocked() {
        composeTestRule.captureMultiDevice("SettingsScreenAppearancePageLocked") {
            TvManiacBackground {
                SettingsScreen(
                    state = appearanceLockedState,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun settingsScreenBehaviorPage() {
        composeTestRule.captureMultiDevice("SettingsScreenBehaviorPage") {
            TvManiacBackground {
                SettingsScreen(
                    state = behaviorState,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun settingsScreenNotificationsPage() {
        composeTestRule.captureMultiDevice("SettingsScreenNotificationsPage") {
            TvManiacBackground {
                SettingsScreen(
                    state = notificationsState,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun settingsScreenNotificationsPageLocked() {
        composeTestRule.captureMultiDevice("SettingsScreenNotificationsPageLocked") {
            TvManiacBackground {
                SettingsScreen(
                    state = notificationsLockedState,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun settingsScreenPrivacyPage() {
        composeTestRule.captureMultiDevice("SettingsScreenPrivacyPage") {
            TvManiacBackground {
                SettingsScreen(
                    state = privacyState,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun settingsScreenInfoPage() {
        composeTestRule.captureMultiDevice("SettingsScreenInfoPage") {
            TvManiacBackground {
                SettingsScreen(
                    state = infoState,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun settingsScreenLicensesPage() {
        composeTestRule.captureMultiDevice("SettingsScreenLicensesPage") {
            TvManiacBackground {
                SettingsScreen(
                    state = licensesState,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun settingsScreenTraktPage() {
        composeTestRule.captureMultiDevice("SettingsScreenTraktPage") {
            TvManiacBackground {
                SettingsScreen(
                    state = accountState,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun settingsScreenTraktPageLoggedOut() {
        composeTestRule.captureMultiDevice("SettingsScreenTraktPageLoggedOut") {
            TvManiacBackground {
                SettingsScreen(
                    state = accountLoggedOutState,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun settingsScreenAccountSwitchAffordance() {
        composeTestRule.captureMultiDevice("SettingsScreenAccountSwitchAffordance") {
            TvManiacBackground {
                SettingsScreen(
                    state = accountSwitchState,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun settingsScreenAccountSwitchDialog() {
        composeTestRule.captureMultiDevice("SettingsScreenAccountSwitchDialog") {
            TvManiacBackground {
                SettingsScreen(
                    state = accountSwitchDialogState,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun settingsScreenAccountSwitching() {
        composeTestRule.captureMultiDevice("SettingsScreenAccountSwitching") {
            TvManiacBackground {
                SettingsScreen(
                    state = accountSwitchingState,
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun settingsScreenAccountLoggingOut() {
        composeTestRule.captureMultiDevice("SettingsScreenAccountLoggingOut") {
            TvManiacBackground {
                SettingsScreen(
                    state = accountLoggingOutState,
                    onAction = {},
                )
            }
        }
    }
}
