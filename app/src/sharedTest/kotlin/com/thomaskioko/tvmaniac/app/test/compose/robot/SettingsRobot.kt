package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import com.thomaskioko.tvmaniac.datastore.api.AutoBackupInterval
import com.thomaskioko.tvmaniac.datastore.api.ImageQuality
import com.thomaskioko.tvmaniac.settings.presenter.ThemeModel
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseRobot
import com.thomaskioko.tvmaniac.testtags.debug.DebugTestTags
import com.thomaskioko.tvmaniac.testtags.settings.SettingsTestTags

private const val DEBUG_MENU_VERSION_TAP_COUNT = 6
private const val DEBUG_MENU_TAP_POLL_TIMEOUT_MILLIS = 200L

@OptIn(ExperimentalTestApi::class)
internal class SettingsRobot(composeUi: ComposeUiTest) : BaseRobot<SettingsRobot>(composeUi) {

    fun assertSettingsScreenDisplayed() = apply {
        assertDisplayed(SettingsTestTags.SCREEN_TEST_TAG)
    }

    fun openDebugMenu() = apply {
        openInfoPage()
        scrollTo(SettingsTestTags.INFO_VERSION_TEXT_TEST_TAG)
        repeat(DEBUG_MENU_VERSION_TAP_COUNT) {
            if (!awaitTag(DebugTestTags.SCREEN_TEST_TAG, timeoutMillis = DEBUG_MENU_TAP_POLL_TIMEOUT_MILLIS)) {
                click(SettingsTestTags.INFO_VERSION_TEXT_TEST_TAG)
            }
        }
        assertDisplayed(DebugTestTags.SCREEN_TEST_TAG)
    }

    fun clickBackButton() = apply {
        click(SettingsTestTags.BACK_BUTTON_TEST_TAG)
    }

    fun openLayoutPage() = apply {
        scrollToListTag(SettingsTestTags.LIST_TEST_TAG, SettingsTestTags.GENERAL_LAYOUT_ROW_TEST_TAG)
        click(SettingsTestTags.GENERAL_LAYOUT_ROW_TEST_TAG)
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

    fun openBackupPage() = apply {
        scrollToListTag(SettingsTestTags.LIST_TEST_TAG, SettingsTestTags.GENERAL_BACKUP_ROW_TEST_TAG)
        click(SettingsTestTags.GENERAL_BACKUP_ROW_TEST_TAG)
    }

    fun assertBackupLockedDisplayed() = apply {
        assertDisplayed(SettingsTestTags.BACKUP_LOCKED_TEST_TAG)
    }

    fun assertBackupExportRowDisplayed() = apply {
        assertDisplayed(SettingsTestTags.BACKUP_EXPORT_ROW_TEST_TAG)
    }

    fun assertBackupExportRowDoesNotExist() = apply {
        assertDoesNotExist(SettingsTestTags.BACKUP_EXPORT_ROW_TEST_TAG)
    }

    fun assertBackupImportRowDisplayed() = apply {
        assertDisplayed(SettingsTestTags.BACKUP_IMPORT_ROW_TEST_TAG)
    }

    fun assertBackupImportRowDoesNotExist() = apply {
        assertDoesNotExist(SettingsTestTags.BACKUP_IMPORT_ROW_TEST_TAG)
    }

    fun clickBackupImportRow() = apply {
        click(SettingsTestTags.BACKUP_IMPORT_ROW_TEST_TAG)
    }

    fun assertBackupRestoreConfirmDisplayed() = apply {
        assertExists(SettingsTestTags.BACKUP_RESTORE_CONFIRM_BUTTON_TEST_TAG)
    }

    fun assertBackupRestoreConfirmDoesNotExist() = apply {
        assertDoesNotExist(SettingsTestTags.BACKUP_RESTORE_CONFIRM_BUTTON_TEST_TAG)
    }

    fun clickBackupRestoreConfirm() = apply {
        click(SettingsTestTags.BACKUP_RESTORE_CONFIRM_BUTTON_TEST_TAG)
    }

    fun assertBackupRestoreDeviceOnlyDisplayed() = apply {
        assertExists(SettingsTestTags.BACKUP_RESTORE_DEVICE_ONLY_BUTTON_TEST_TAG)
    }

    fun clickBackupRestoreDeviceOnly() = apply {
        click(SettingsTestTags.BACKUP_RESTORE_DEVICE_ONLY_BUTTON_TEST_TAG)
    }

    fun clickBackupRestoreDismiss() = apply {
        click(SettingsTestTags.BACKUP_RESTORE_DISMISS_BUTTON_TEST_TAG)
    }

    fun scrollToAutoBackupToggle() = apply {
        scrollToListTag(SettingsTestTags.LIST_TEST_TAG, SettingsTestTags.AUTO_BACKUP_TOGGLE_TEST_TAG)
    }

    fun assertAutoBackupEnabled() = apply {
        assertChecked(SettingsTestTags.AUTO_BACKUP_TOGGLE_TEST_TAG)
    }

    fun assertAutoBackupDisabled() = apply {
        assertUnchecked(SettingsTestTags.AUTO_BACKUP_TOGGLE_TEST_TAG)
    }

    fun clickAutoBackupToggle() = apply {
        click(SettingsTestTags.AUTO_BACKUP_TOGGLE_TEST_TAG)
    }

    fun assertAutoBackupLocationRowDisplayed() = apply {
        assertExists(SettingsTestTags.AUTO_BACKUP_LOCATION_ROW_TEST_TAG)
    }

    fun assertAutoBackupLocationRowDoesNotExist() = apply {
        assertDoesNotExist(SettingsTestTags.AUTO_BACKUP_LOCATION_ROW_TEST_TAG)
    }

    fun assertAutoBackupNowRowDisplayed() = apply {
        assertExists(SettingsTestTags.AUTO_BACKUP_NOW_ROW_TEST_TAG)
    }

    fun assertAutoBackupNowRowDoesNotExist() = apply {
        assertDoesNotExist(SettingsTestTags.AUTO_BACKUP_NOW_ROW_TEST_TAG)
    }

    fun scrollToAutoBackupScheduleChip(interval: AutoBackupInterval) = apply {
        scrollToListTag(
            SettingsTestTags.LIST_TEST_TAG,
            SettingsTestTags.autoBackupScheduleChip(interval.name),
        )
    }

    fun clickAutoBackupScheduleChip(interval: AutoBackupInterval) = apply {
        click(SettingsTestTags.autoBackupScheduleChip(interval.name))
    }

    fun assertAutoBackupScheduleSelected(interval: AutoBackupInterval) = apply {
        assertSelected(SettingsTestTags.autoBackupScheduleChip(interval.name))
    }

    fun assertAutoBackupScheduleNotSelected(interval: AutoBackupInterval) = apply {
        assertNotSelected(SettingsTestTags.autoBackupScheduleChip(interval.name))
    }

    fun clickBackupFileNameRow() = apply {
        scrollToListTag(SettingsTestTags.LIST_TEST_TAG, SettingsTestTags.BACKUP_FILE_NAME_ROW_TEST_TAG)
        click(SettingsTestTags.BACKUP_FILE_NAME_ROW_TEST_TAG)
    }

    fun assertBackupFileName(name: String) = apply {
        assertNodeHasText(SettingsTestTags.BACKUP_FILE_NAME_ROW_TEST_TAG, name)
    }

    fun replaceBackupFileName(name: String) = apply {
        replaceText(SettingsTestTags.BACKUP_FILE_NAME_FIELD_TEST_TAG, name)
    }

    fun clickBackupFileNameSave() = apply {
        click(SettingsTestTags.BACKUP_FILE_NAME_SAVE_BUTTON_TEST_TAG)
    }

    fun clickBackupFileNameCancel() = apply {
        click(SettingsTestTags.BACKUP_FILE_NAME_CANCEL_BUTTON_TEST_TAG)
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

    fun scrollToThemesLocked() = apply {
        scrollToListTag(SettingsTestTags.LIST_TEST_TAG, SettingsTestTags.THEMES_LOCKED)
    }

    fun assertThemeSwatchDoesNotExist(theme: ThemeModel) = apply {
        assertDoesNotExist(SettingsTestTags.themeSwatch(theme.name))
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

    fun scrollToQuickRateToggle() = apply {
        scrollToListTag(
            SettingsTestTags.LIST_TEST_TAG,
            SettingsTestTags.QUICK_RATE_TOGGLE_TEST_TAG,
        )
    }

    fun assertQuickRateEnabled() = apply {
        assertChecked(SettingsTestTags.QUICK_RATE_TOGGLE_TEST_TAG)
    }

    fun assertQuickRateDisabled() = apply {
        assertUnchecked(SettingsTestTags.QUICK_RATE_TOGGLE_TEST_TAG)
    }

    fun clickQuickRateToggle() = apply {
        click(SettingsTestTags.QUICK_RATE_TOGGLE_TEST_TAG)
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
