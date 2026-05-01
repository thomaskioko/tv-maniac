package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseRobot
import com.thomaskioko.tvmaniac.testtags.component.DesignComponentTestTags
import com.thomaskioko.tvmaniac.testtags.notifications.NotificationRationaleTestTags

internal class RootRobot(composeTestRule: ComposeContentTestRule) : BaseRobot(composeTestRule) {

    /**
     * Asserts that an error snackbar with [message] is displayed.
     */
    fun verifyErrorSnackbarShown(message: String) {
        verifyTextShown(text = message, substring = true)
    }

    /**
     * Asserts that no snackbar is currently displayed. Polls for [timeoutMillis] to make sure a
     * delayed snackbar (e.g. from an async sync) has time to appear before passing.
     */
    fun verifyNoSnackbarShown(timeoutMillis: Long = 2_000) {
        verifyTagHidden(tag = DesignComponentTestTags.SNACKBAR_TEST_TAG, timeoutMillis = timeoutMillis)
    }

    /**
     * Dismisses the snackbar by swiping it right.
     */
    fun dismissSnackbar() {
        if (awaitTag(tag = DesignComponentTestTags.SNACKBAR_TEST_TAG)) {
            swipeRight(DesignComponentTestTags.SNACKBAR_TEST_TAG)
            verifyTagHidden(DesignComponentTestTags.SNACKBAR_TEST_TAG)
        }
    }

    fun verifyNotificationRationaleIsShown() {
        verifyTagExists(NotificationRationaleTestTags.BOTTOM_SHEET)
    }

    fun verifyNotificationRationaleIsHidden() {
        verifyTagHidden(NotificationRationaleTestTags.BOTTOM_SHEET)
    }

    /**
     * Dismisses rationale bottom sheet.
     */
    fun verifyNotificationRationaleIsShownAndDismissed() {
        if (awaitTag(tag = NotificationRationaleTestTags.BOTTOM_SHEET)) {
            click(NotificationRationaleTestTags.DISMISS_BUTTON)
            verifyTagHidden(NotificationRationaleTestTags.BOTTOM_SHEET)
        }
    }

    fun acceptNotificationRationale() {
        click(NotificationRationaleTestTags.ENABLE_BUTTON)
    }
}
