package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import com.thomaskioko.tvmaniac.datastore.api.ImageQuality
import com.thomaskioko.tvmaniac.settings.presenter.ThemeModel
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseRobot
import com.thomaskioko.tvmaniac.testtags.settings.SettingsTestTags

@OptIn(ExperimentalTestApi::class)
internal class SettingsRobot(composeUi: ComposeUiTest) : BaseRobot<SettingsRobot>(composeUi) {

    fun assertSettingsScreenDisplayed() = apply {
        assertDisplayed(SettingsTestTags.SCREEN_TEST_TAG)
    }

    fun clickBackButton() = apply {
        click(SettingsTestTags.BACK_BUTTON_TEST_TAG)
    }

    fun assertImageQualitySelected(quality: ImageQuality) = apply {
        assertSelected(SettingsTestTags.imageQualityChip(quality.name))
    }

    fun assertImageQualityNotSelected(quality: ImageQuality) = apply {
        assertNotSelected(SettingsTestTags.imageQualityChip(quality.name))
    }

    fun clickImageQualityChip(quality: ImageQuality) = apply {
        click(SettingsTestTags.imageQualityChip(quality.name))
    }

    fun scrollToImageQualityChip(quality: ImageQuality) = apply {
        scrollToListTag(
            SettingsTestTags.LIST_TEST_TAG,
            SettingsTestTags.imageQualityChip(quality.name),
        )
    }

    fun scrollToTraktAccountRow() = apply {
        scrollToListTag(SettingsTestTags.LIST_TEST_TAG, SettingsTestTags.TRAKT_ACCOUNT_ROW_TEST_TAG)
    }

    fun clickTraktAccountRow() = apply {
        click(SettingsTestTags.TRAKT_ACCOUNT_ROW_TEST_TAG)
    }

    fun assertLogoutDialogDisplayed() = apply {
        assertExists(SettingsTestTags.LOGOUT_DIALOG_CONFIRM_BUTTON_TEST_TAG)
    }

    fun assertLogoutDialogDoesNotExist() = apply {
        assertDoesNotExist(SettingsTestTags.LOGOUT_DIALOG_CONFIRM_BUTTON_TEST_TAG)
    }

    fun clickLogoutConfirm() = apply {
        click(SettingsTestTags.LOGOUT_DIALOG_CONFIRM_BUTTON_TEST_TAG)
    }

    fun clickLogoutDismiss() = apply {
        click(SettingsTestTags.LOGOUT_DIALOG_DISMISS_BUTTON_TEST_TAG)
    }

    fun scrollToThemeSwatch(theme: ThemeModel) = apply {
        scrollToListTag(SettingsTestTags.LIST_TEST_TAG, SettingsTestTags.themeSwatch(theme.name))
    }

    fun assertThemeSwatchSelected(theme: ThemeModel) = apply {
        assertSelected(SettingsTestTags.themeSwatch(theme.name))
    }

    fun assertThemeSwatchNotSelected(theme: ThemeModel) = apply {
        assertNotSelected(SettingsTestTags.themeSwatch(theme.name))
    }

    fun clickThemeSwatch(theme: ThemeModel) = apply {
        click(SettingsTestTags.themeSwatch(theme.name))
    }

    fun scrollToEpisodeNotificationsToggle() = apply {
        scrollToListTag(
            SettingsTestTags.LIST_TEST_TAG,
            SettingsTestTags.EPISODE_NOTIFICATIONS_TOGGLE_TEST_TAG,
        )
    }

    fun assertEpisodeNotificationsEnabled() = apply {
        assertChecked(SettingsTestTags.EPISODE_NOTIFICATIONS_TOGGLE_TEST_TAG)
    }

    fun assertEpisodeNotificationsDisabled() = apply {
        assertUnchecked(SettingsTestTags.EPISODE_NOTIFICATIONS_TOGGLE_TEST_TAG)
    }

    fun clickEpisodeNotificationsToggle() = apply {
        click(SettingsTestTags.EPISODE_NOTIFICATIONS_TOGGLE_TEST_TAG)
    }
}
