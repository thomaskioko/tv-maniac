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

    fun openAppearancePage() = apply {
        scrollToListTag(SettingsTestTags.LIST_TEST_TAG, SettingsTestTags.GENERAL_APPEARANCE_ROW_TEST_TAG)
        click(SettingsTestTags.GENERAL_APPEARANCE_ROW_TEST_TAG)
    }

    fun openBehaviorPage() = apply {
        scrollToListTag(SettingsTestTags.LIST_TEST_TAG, SettingsTestTags.GENERAL_BEHAVIOR_ROW_TEST_TAG)
        click(SettingsTestTags.GENERAL_BEHAVIOR_ROW_TEST_TAG)
    }

    fun openNotificationsPage() = apply {
        scrollToListTag(SettingsTestTags.LIST_TEST_TAG, SettingsTestTags.GENERAL_NOTIFICATIONS_ROW_TEST_TAG)
        click(SettingsTestTags.GENERAL_NOTIFICATIONS_ROW_TEST_TAG)
    }

    fun openPrivacyPage() = apply {
        scrollToListTag(SettingsTestTags.LIST_TEST_TAG, SettingsTestTags.GENERAL_PRIVACY_ROW_TEST_TAG)
        click(SettingsTestTags.GENERAL_PRIVACY_ROW_TEST_TAG)
    }

    fun openInfoPage() = apply {
        scrollToListTag(SettingsTestTags.LIST_TEST_TAG, SettingsTestTags.ABOUT_INFO_ROW_TEST_TAG)
        click(SettingsTestTags.ABOUT_INFO_ROW_TEST_TAG)
    }

    fun openLicensesPage() = apply {
        scrollToListTag(SettingsTestTags.LIST_TEST_TAG, SettingsTestTags.ABOUT_LICENSES_ROW_TEST_TAG)
        click(SettingsTestTags.ABOUT_LICENSES_ROW_TEST_TAG)
    }

    fun openTraktPage() = apply {
        scrollToListTag(SettingsTestTags.LIST_TEST_TAG, SettingsTestTags.ACCOUNT_TRAKT_ROW_TEST_TAG)
        click(SettingsTestTags.ACCOUNT_TRAKT_ROW_TEST_TAG)
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

    fun assertTraktAccountButtonDisplayed() = apply {
        assertDisplayed(SettingsTestTags.TRAKT_ACCOUNT_ROW_TEST_TAG)
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

    fun scrollToSwitchProviderButton() = apply {
        scrollToListTag(SettingsTestTags.LIST_TEST_TAG, SettingsTestTags.SWITCH_PROVIDER_BUTTON_TEST_TAG)
    }

    fun assertSwitchProviderButtonDisplayed() = apply {
        assertDisplayed(SettingsTestTags.SWITCH_PROVIDER_BUTTON_TEST_TAG)
    }

    fun assertSwitchProviderButtonDoesNotExist() = apply {
        assertDoesNotExist(SettingsTestTags.SWITCH_PROVIDER_BUTTON_TEST_TAG)
    }

    fun clickSwitchProviderButton() = apply {
        click(SettingsTestTags.SWITCH_PROVIDER_BUTTON_TEST_TAG)
    }

    fun assertSwitchDialogDisplayed() = apply {
        assertExists(SettingsTestTags.SWITCH_DIALOG_CONFIRM_BUTTON_TEST_TAG)
    }

    fun assertSwitchDialogDoesNotExist() = apply {
        assertDoesNotExist(SettingsTestTags.SWITCH_DIALOG_CONFIRM_BUTTON_TEST_TAG)
    }

    fun clickSwitchDialogConfirm() = apply {
        click(SettingsTestTags.SWITCH_DIALOG_CONFIRM_BUTTON_TEST_TAG)
    }

    fun clickSwitchDialogDismiss() = apply {
        click(SettingsTestTags.SWITCH_DIALOG_DISMISS_BUTTON_TEST_TAG)
    }

    fun assertSwitchingIndicatorDisplayed() = apply {
        assertExists(SettingsTestTags.SWITCHING_INDICATOR_TEST_TAG)
    }
}
