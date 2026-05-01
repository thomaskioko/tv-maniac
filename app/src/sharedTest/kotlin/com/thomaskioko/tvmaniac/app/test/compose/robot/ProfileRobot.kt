package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseRobot
import com.thomaskioko.tvmaniac.testtags.profile.ProfileTestTags

internal class ProfileRobot(composeTestRule: ComposeContentTestRule) : BaseRobot(composeTestRule) {

    fun assertProfileScreenDisplayed() {
        assertDisplayed(ProfileTestTags.SCREEN_TEST_TAG)
    }

    fun assertSignInButtonDisplayed() {
        scrollTo(ProfileTestTags.SIGN_IN_BUTTON_TEST_TAG)
        assertDisplayed(ProfileTestTags.SIGN_IN_BUTTON_TEST_TAG)
    }

    fun assertUserCardDisplayed(slug: String) {
        assertDisplayed(ProfileTestTags.userCard(slug))
    }

    fun clickSignInButton() {
        click(ProfileTestTags.SIGN_IN_BUTTON_TEST_TAG)
    }

    fun clickSettingsButton() {
        click(ProfileTestTags.SETTINGS_BUTTON_TEST_TAG)
    }
}
