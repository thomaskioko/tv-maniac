package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import com.thomaskioko.tvmaniac.datastore.api.ImageQuality
import com.thomaskioko.tvmaniac.settings.presenter.ThemeModel
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseRobot
import com.thomaskioko.tvmaniac.testtags.settings.SettingsTestTags

@OptIn(ExperimentalTestApi::class)
internal class SettingsRobot(composeUi: ComposeUiTest) : BaseRobot(composeUi) {

    fun assertSettingsScreenDisplayed() {
        assertDisplayed(SettingsTestTags.SCREEN_TEST_TAG)
    }

    fun assertImageQualitySelected(quality: ImageQuality) {
        assertSelected(SettingsTestTags.imageQualityChip(quality.name))
    }

    fun assertImageQualityNotSelected(quality: ImageQuality) {
        assertNotSelected(SettingsTestTags.imageQualityChip(quality.name))
    }

    fun clickImageQualityChip(quality: ImageQuality) {
        click(SettingsTestTags.imageQualityChip(quality.name), useSemanticsAction = true)
    }

    fun scrollToImageQualityChip(quality: ImageQuality) {
        scrollToListTag(
            SettingsTestTags.LIST_TEST_TAG,
            SettingsTestTags.imageQualityChip(quality.name),
        )
    }

    fun scrollToTraktAccountRow() {
        scrollToListTag(SettingsTestTags.LIST_TEST_TAG, SettingsTestTags.TRAKT_ACCOUNT_ROW_TEST_TAG)
    }

    fun clickTraktAccountRow() {
        click(SettingsTestTags.TRAKT_ACCOUNT_ROW_TEST_TAG, useSemanticsAction = true)
    }

    fun assertLogoutDialogDisplayed() {
        assertExists(SettingsTestTags.LOGOUT_DIALOG_CONFIRM_BUTTON_TEST_TAG)
    }

    fun assertLogoutDialogDoesNotExist() {
        assertDoesNotExist(SettingsTestTags.LOGOUT_DIALOG_CONFIRM_BUTTON_TEST_TAG)
    }

    fun clickLogoutConfirm() {
        click(SettingsTestTags.LOGOUT_DIALOG_CONFIRM_BUTTON_TEST_TAG, useSemanticsAction = true)
    }

    fun clickLogoutDismiss() {
        click(SettingsTestTags.LOGOUT_DIALOG_DISMISS_BUTTON_TEST_TAG, useSemanticsAction = true)
    }

    fun scrollToThemeSwatch(theme: ThemeModel) {
        scrollToListTag(SettingsTestTags.LIST_TEST_TAG, SettingsTestTags.themeSwatch(theme.name))
    }

    fun assertThemeSwatchSelected(theme: ThemeModel) {
        assertSelected(SettingsTestTags.themeSwatch(theme.name))
    }

    fun assertThemeSwatchNotSelected(theme: ThemeModel) {
        assertNotSelected(SettingsTestTags.themeSwatch(theme.name))
    }

    fun clickThemeSwatch(theme: ThemeModel) {
        click(SettingsTestTags.themeSwatch(theme.name), useSemanticsAction = true)
    }

    fun scrollToEpisodeNotificationsToggle() {
        scrollToListTag(
            SettingsTestTags.LIST_TEST_TAG,
            SettingsTestTags.EPISODE_NOTIFICATIONS_TOGGLE_TEST_TAG,
        )
    }

    fun assertEpisodeNotificationsEnabled() {
        assertChecked(SettingsTestTags.EPISODE_NOTIFICATIONS_TOGGLE_TEST_TAG)
    }

    fun assertEpisodeNotificationsDisabled() {
        assertUnchecked(SettingsTestTags.EPISODE_NOTIFICATIONS_TOGGLE_TEST_TAG)
    }

    fun clickEpisodeNotificationsToggle() {
        click(SettingsTestTags.EPISODE_NOTIFICATIONS_TOGGLE_TEST_TAG, useSemanticsAction = true)
    }
}
