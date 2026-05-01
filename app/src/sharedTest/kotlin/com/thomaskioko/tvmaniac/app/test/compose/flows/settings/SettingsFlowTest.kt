package com.thomaskioko.tvmaniac.app.test.compose.flows.settings

import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import com.thomaskioko.tvmaniac.datastore.api.ImageQuality
import org.junit.Test

internal class SettingsFlowTest : BaseAppFlowTest() {

    @Test
    fun givenSettings_whenImageQualitySelected_thenSelectionIsPersisted() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        discoverRobot.assertDiscoverScreenDisplayed()

        scenarios.stubUsersMeUnauthorized()

        homeRobot.clickProfileTab()
        profileRobot.assertSignInButtonDisplayed()
        profileRobot.clickSettingsButton()
        settingsRobot.assertSettingsScreenDisplayed()

        settingsRobot.scrollToImageQualityChip(ImageQuality.HIGH)
        settingsRobot.assertImageQualitySelected(ImageQuality.AUTO)
        settingsRobot.assertImageQualityNotSelected(ImageQuality.HIGH)

        settingsRobot.clickImageQualityChip(ImageQuality.HIGH)

        settingsRobot.assertImageQualitySelected(ImageQuality.HIGH)
        settingsRobot.assertImageQualityNotSelected(ImageQuality.AUTO)
    }

    @Test
    fun givenAuthenticatedUser_whenTraktAccountClicked_thenShowsLogoutDialog() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        discoverRobot.assertDiscoverScreenDisplayed()

        scenarios.signInAndDismissRationale()

        homeRobot.clickProfileTab()
        profileRobot.assertUserCardDisplayed("integration-test-user")
        profileRobot.clickSettingsButton()
        settingsRobot.assertSettingsScreenDisplayed()

        settingsRobot.scrollToTraktAccountRow()
        settingsRobot.clickTraktAccountRow()

        settingsRobot.assertLogoutDialogDisplayed()
        settingsRobot.clickLogoutDismiss()
        settingsRobot.assertLogoutDialogDoesNotExist()
    }
}
