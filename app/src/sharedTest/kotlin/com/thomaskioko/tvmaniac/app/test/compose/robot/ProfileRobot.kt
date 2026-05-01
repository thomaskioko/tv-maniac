package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseRobot
import com.thomaskioko.tvmaniac.testtags.profile.ProfileTestTags

internal class ProfileRobot(composeTestRule: ComposeContentTestRule) : BaseRobot(composeTestRule) {

    fun verifyProfileScreenIsShown() {
        verifyTagShown(ProfileTestTags.SCREEN_TEST_TAG)
    }

    fun verifySignInButtonIsShown() {
        scrollTo(ProfileTestTags.SIGN_IN_BUTTON_TEST_TAG)
        verifyTagShown(ProfileTestTags.SIGN_IN_BUTTON_TEST_TAG)
    }

    fun verifyUserCardIsShown(slug: String) {
        verifyTagShown(ProfileTestTags.userCard(slug))
    }

    fun clickSignInButton() {
        click(ProfileTestTags.SIGN_IN_BUTTON_TEST_TAG)
    }

    fun clickSettingsButton() {
        click(ProfileTestTags.SETTINGS_BUTTON_TEST_TAG)
    }
}
