package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseRobot
import com.thomaskioko.tvmaniac.testtags.profile.ProfileTestTags

@OptIn(ExperimentalTestApi::class)
internal class ProfileRobot(composeUi: ComposeUiTest) : BaseRobot(composeUi) {

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
