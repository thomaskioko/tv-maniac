package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.thomaskioko.tvmaniac.datastore.api.ImageQuality
import com.thomaskioko.tvmaniac.settings.presenter.ThemeModel
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseRobot
import com.thomaskioko.tvmaniac.testing.integration.ui.TIMEOUT_MILLIS
import com.thomaskioko.tvmaniac.testing.integration.ui.isChecked
import com.thomaskioko.tvmaniac.testing.integration.ui.isNotSelected
import com.thomaskioko.tvmaniac.testing.integration.ui.isSelected
import com.thomaskioko.tvmaniac.testing.integration.ui.isUnchecked
import com.thomaskioko.tvmaniac.testtags.settings.SettingsTestTags

internal class SettingsRobot(composeTestRule: ComposeContentTestRule) : BaseRobot(composeTestRule) {

    fun assertSettingsScreenDisplayed() {
        assertDisplayed(SettingsTestTags.SCREEN_TEST_TAG)
    }

    fun assertImageQualitySelected(quality: ImageQuality) {
        composeTestRule.isSelected(
            SettingsTestTags.imageQualityChip(quality.name),
            timeoutMillis = TIMEOUT_MILLIS,
        )
    }

    fun assertImageQualityNotSelected(quality: ImageQuality) {
        composeTestRule.isNotSelected(
            tag = SettingsTestTags.imageQualityChip(quality.name),
            timeoutMillis = TIMEOUT_MILLIS,
        )
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
        composeTestRule.isSelected(
            tag = SettingsTestTags.themeSwatch(theme.name),
            timeoutMillis = TIMEOUT_MILLIS,
        )
    }

    fun assertThemeSwatchNotSelected(theme: ThemeModel) {
        composeTestRule.isNotSelected(
            tag = SettingsTestTags.themeSwatch(theme.name),
            timeoutMillis = TIMEOUT_MILLIS,
        )
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
        composeTestRule.isChecked(
            tag = SettingsTestTags.EPISODE_NOTIFICATIONS_TOGGLE_TEST_TAG,
            timeoutMillis = TIMEOUT_MILLIS,
        )
    }

    fun assertEpisodeNotificationsDisabled() {
        composeTestRule.isUnchecked(
            tag = SettingsTestTags.EPISODE_NOTIFICATIONS_TOGGLE_TEST_TAG,
            timeoutMillis = TIMEOUT_MILLIS,
        )
    }

    fun clickEpisodeNotificationsToggle() {
        click(SettingsTestTags.EPISODE_NOTIFICATIONS_TOGGLE_TEST_TAG, useSemanticsAction = true)
    }
}
