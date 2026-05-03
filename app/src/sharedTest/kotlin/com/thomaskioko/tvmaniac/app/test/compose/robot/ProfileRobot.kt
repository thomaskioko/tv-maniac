package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseRobot
import com.thomaskioko.tvmaniac.testtags.profile.ProfileTestTags

@OptIn(ExperimentalTestApi::class)
internal class ProfileRobot(composeUi: ComposeUiTest) : BaseRobot<ProfileRobot>(composeUi) {

    fun assertProfileScreenDisplayed() = apply {
        assertDisplayed(ProfileTestTags.SCREEN_TEST_TAG)
    }

    fun assertSignInButtonDisplayed() = apply {
        assertDisplayed(ProfileTestTags.SIGN_IN_BUTTON_TEST_TAG)
    }

    fun scrollToSignInButton() = apply {
        scrollTo(ProfileTestTags.SIGN_IN_BUTTON_TEST_TAG)
    }

    fun assertUserCardDisplayed(slug: String) = apply {
        assertDisplayed(ProfileTestTags.userCard(slug))
    }

    fun clickSignInButton() = apply {
        click(ProfileTestTags.SIGN_IN_BUTTON_TEST_TAG)
    }

    fun clickSettingsButton() = apply {
        click(ProfileTestTags.SETTINGS_BUTTON_TEST_TAG)
    }
}
