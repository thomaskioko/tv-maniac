package com.thomaskioko.tvmaniac.seasondetails.roborrazi

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import com.thomaskioko.tvmaniac.compose.components.TvManiacBackground
import com.thomaskioko.tvmaniac.screenshottests.captureMultiDevice
import com.thomaskioko.tvmaniac.settings.ui.SettingsScreen
import com.thomaskioko.tvmaniac.settings.ui.appearanceState
import com.thomaskioko.tvmaniac.settings.ui.behaviorState
import com.thomaskioko.tvmaniac.settings.ui.defaultState
import com.thomaskioko.tvmaniac.settings.ui.infoState
import com.thomaskioko.tvmaniac.settings.ui.licensesState
import com.thomaskioko.tvmaniac.settings.ui.loggedInState
import com.thomaskioko.tvmaniac.settings.ui.notificationsState
import com.thomaskioko.tvmaniac.settings.ui.privacyState
import com.thomaskioko.tvmaniac.settings.ui.traktState
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
                    state = traktState,
                    onAction = {},
                )
            }
        }
    }
}
