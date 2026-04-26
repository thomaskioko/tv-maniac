package com.thomaskioko.tvmaniac.app.test.compose.flows.settings

import com.thomaskioko.tvmaniac.app.test.util.BaseAppRobolectricTest
import com.thomaskioko.tvmaniac.datastore.api.ImageQuality
import com.thomaskioko.tvmaniac.testing.integration.ui.NetworkResponse
import org.junit.Before
import kotlin.test.Test

internal class SettingsFlowTest : BaseAppRobolectricTest() {

    @Before
    fun setUp() {
        scenarios.stubDiscoverBrowse()
    }

    private fun stubUsersMeUnauthorized() {
        environment.stubber.stub(path = "/users/me", response = NetworkResponse.Error(401))
    }

    @Test
    fun `should persist image quality selection across presenter cycle`() {
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
    fun `should show logout dialog when authenticated trakt account row is tapped`() {
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
