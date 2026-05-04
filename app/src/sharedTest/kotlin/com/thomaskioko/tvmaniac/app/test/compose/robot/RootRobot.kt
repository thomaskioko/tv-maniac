package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseRobot
import com.thomaskioko.tvmaniac.testtags.component.DesignComponentTestTags
import com.thomaskioko.tvmaniac.testtags.notifications.NotificationRationaleTestTags

@OptIn(ExperimentalTestApi::class)
internal class RootRobot(composeUi: ComposeUiTest) : BaseRobot<RootRobot>(composeUi) {

    /**
     * Asserts that an error snackbar with [message] is displayed.
     */
    fun assertErrorSnackbarDisplayed(message: String) = apply {
        assertTextDisplayed(text = message, substring = true)
    }

    /**
     * Asserts that no snackbar is currently displayed. Polls for [timeoutMillis] to make sure a
     * delayed snackbar (e.g. from an async sync) has time to appear before passing.
     */
    fun assertNoSnackbarDisplayed(timeoutMillis: Long = 2_000) = apply {
        assertDoesNotExist(tag = DesignComponentTestTags.SNACKBAR_TEST_TAG, timeoutMillis = timeoutMillis)
    }

    /**
     * Dismisses the snackbar by swiping it right.
     */
    fun dismissSnackbar() = apply {
        if (awaitTag(tag = DesignComponentTestTags.SNACKBAR_TEST_TAG)) {
            swipeRight(DesignComponentTestTags.SNACKBAR_TEST_TAG)
        }
    }

    fun assertNotificationRationaleDisplayed() = apply {
        assertExists(NotificationRationaleTestTags.BOTTOM_SHEET)
    }

    fun assertNotificationRationaleDoesNotExist() = apply {
        assertDoesNotExist(NotificationRationaleTestTags.BOTTOM_SHEET)
    }

    /**
     * Dismisses rationale bottom sheet.
     */
    fun dismissNotificationRationale() = apply {
        if (awaitTag(NotificationRationaleTestTags.BOTTOM_SHEET)) {
            click(NotificationRationaleTestTags.DISMISS_BUTTON)
        }
    }

    fun acceptNotificationRationale() = apply {
        click(NotificationRationaleTestTags.ENABLE_BUTTON)
    }
}
