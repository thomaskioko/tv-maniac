package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.thomaskioko.tvmaniac.datastore.api.ImageQuality
import com.thomaskioko.tvmaniac.testing.integration.ui.robot.BaseRobot
import com.thomaskioko.tvmaniac.testing.integration.ui.util.isNotSelected
import com.thomaskioko.tvmaniac.testing.integration.ui.util.isSelected
import com.thomaskioko.tvmaniac.testtags.settings.SettingsTestTags

internal class SettingsRobot(composeTestRule: ComposeContentTestRule) : BaseRobot(composeTestRule) {

    fun verifySettingsScreenIsShown() {
        verifyTagShown(SettingsTestTags.SCREEN_TEST_TAG)
    }

    fun verifyImageQualitySelected(quality: ImageQuality) {
        composeTestRule.isSelected(SettingsTestTags.imageQualityChip(quality.name))
    }

    fun verifyImageQualityNotSelected(quality: ImageQuality) {
        composeTestRule.isNotSelected(SettingsTestTags.imageQualityChip(quality.name))
    }

    fun clickImageQualityChip(quality: ImageQuality) {
        click(SettingsTestTags.imageQualityChip(quality.name), useSemanticsAction = true)
    }

    fun scrollToImageQualityChip(quality: ImageQuality) {
        scrollToListTag(SettingsTestTags.LIST_TEST_TAG, SettingsTestTags.imageQualityChip(quality.name))
    }

    fun scrollToTraktAccountRow() {
        scrollToListTag(SettingsTestTags.LIST_TEST_TAG, SettingsTestTags.TRAKT_ACCOUNT_ROW_TEST_TAG)
    }

    fun clickTraktAccountRow() {
        click(SettingsTestTags.TRAKT_ACCOUNT_ROW_TEST_TAG, useSemanticsAction = true)
    }

    fun verifyLogoutDialogIsShown() {
        verifyTagExists(SettingsTestTags.LOGOUT_DIALOG_CONFIRM_BUTTON_TEST_TAG)
    }

    fun verifyLogoutDialogIsHidden() {
        verifyTagHidden(SettingsTestTags.LOGOUT_DIALOG_CONFIRM_BUTTON_TEST_TAG)
    }

    fun clickLogoutConfirm() {
        click(SettingsTestTags.LOGOUT_DIALOG_CONFIRM_BUTTON_TEST_TAG, useSemanticsAction = true)
    }

    fun clickLogoutDismiss() {
        click(SettingsTestTags.LOGOUT_DIALOG_DISMISS_BUTTON_TEST_TAG, useSemanticsAction = true)
    }
}
