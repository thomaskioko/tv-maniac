package com.thomaskioko.tvmaniac.app.test.compose.flows.settings

import com.thomaskioko.tvmaniac.app.test.util.BaseAppFlowTest
import com.thomaskioko.tvmaniac.datastore.api.ImageQuality
import com.thomaskioko.tvmaniac.testing.integration.ui.NetworkResponse
import org.junit.Before
import org.junit.Test

internal class SettingsFlowTest : BaseAppFlowTest() {

    @Before
    fun setUp() {
        scenarios.stubDiscoverBrowse()
    }

    private fun stubUsersMeUnauthorized() {
        environment.stubber.stub(path = "/users/me", response = NetworkResponse.Error(401))
    }

    @Test
    fun shouldPersistImageQualitySelectionAcrossPresenterCycle() {
        stubUsersMeUnauthorized()

        homeRobot.clickProfileTab()
        profileRobot.verifySignInButtonIsShown()
        profileRobot.clickSettingsButton()
        settingsRobot.verifySettingsScreenIsShown()

        settingsRobot.scrollToImageQualityChip(ImageQuality.HIGH)
        settingsRobot.verifyImageQualitySelected(ImageQuality.AUTO)
        settingsRobot.verifyImageQualityNotSelected(ImageQuality.HIGH)

        settingsRobot.clickImageQualityChip(ImageQuality.HIGH)

        settingsRobot.verifyImageQualitySelected(ImageQuality.HIGH)
        settingsRobot.verifyImageQualityNotSelected(ImageQuality.AUTO)
    }

    @Test
    fun shouldShowLogoutDialogWhenAuthenticatedTraktAccountRowIsTapped() {
        scenarios.auth.stubLoggedInUser()
        scenarios.profile.stubProfileSyncEndpoints()

        homeRobot.clickProfileTab()
        profileRobot.verifyUserCardIsShown("integration-test-user")
        profileRobot.clickSettingsButton()
        settingsRobot.verifySettingsScreenIsShown()

        settingsRobot.scrollToTraktAccountRow()
        settingsRobot.clickTraktAccountRow()

        settingsRobot.verifyLogoutDialogIsShown()
        settingsRobot.clickLogoutDismiss()
        settingsRobot.verifyLogoutDialogIsHidden()
    }
}
