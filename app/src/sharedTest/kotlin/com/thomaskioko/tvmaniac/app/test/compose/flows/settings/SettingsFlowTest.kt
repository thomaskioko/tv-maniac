package com.thomaskioko.tvmaniac.app.test.compose.flows.settings

import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import com.thomaskioko.tvmaniac.datastore.api.ImageQuality
import org.junit.Before
import org.junit.Test

internal class SettingsFlowTest : BaseAppFlowTest() {

    @Before
    fun setUp() {
        scenarios.discover.stubBrowseGraph()
    }

    @Test
    fun shouldPersistImageQualitySelectionAcrossPresenterCycle() {
        discoverRobot.verifyDiscoverScreenIsShown()

        scenarios.stubUsersMeUnauthorized()

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
        discoverRobot.verifyDiscoverScreenIsShown()

        scenarios.signInAndDismissRationale()

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
