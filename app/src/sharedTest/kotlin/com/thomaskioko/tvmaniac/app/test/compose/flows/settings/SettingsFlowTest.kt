package com.thomaskioko.tvmaniac.app.test.compose.flows.settings

import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import com.thomaskioko.tvmaniac.datastore.api.ImageQuality
import com.thomaskioko.tvmaniac.testtags.home.HomeTestTags
import org.junit.Test

internal class SettingsFlowTest : BaseAppFlowTest() {

    @Test
    fun givenSettings_whenImageQualitySelected_thenSelectionIsPersisted() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        discoverRobot.assertDiscoverScreenDisplayed()

        scenarios.stubUsersMeUnauthorized()

        homeRobot.clickProfileTab()
            .assertTabSelected(HomeTestTags.PROFILE_TAB)

        profileRobot
            .scrollToSignInButton()
            .assertSignInButtonDisplayed()
            .clickSettingsButton()

        settingsRobot
            .assertSettingsScreenDisplayed()
            .scrollToImageQualityChip(ImageQuality.HIGH)
            .assertImageQualitySelected(ImageQuality.AUTO)
            .assertImageQualityNotSelected(ImageQuality.HIGH)
            .clickImageQualityChip(ImageQuality.HIGH)
            .assertImageQualitySelected(ImageQuality.HIGH)
            .assertImageQualityNotSelected(ImageQuality.AUTO)
    }

    @Test
    fun givenAuthenticatedUser_whenTraktAccountClicked_thenShowsLogoutDialog() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        discoverRobot.assertDiscoverScreenDisplayed()

        scenarios.signInAndDismissRationale()

        homeRobot
            .clickProfileTab()
            .assertTabSelected(HomeTestTags.PROFILE_TAB)

        profileRobot
            .assertUserCardDisplayed("integration-test-user")
            .clickSettingsButton()

        settingsRobot
            .assertSettingsScreenDisplayed()
            .scrollToTraktAccountRow()
            .clickTraktAccountRow()
            .assertLogoutDialogDisplayed()
            .clickLogoutDismiss()
            .assertLogoutDialogDoesNotExist()
    }
}
