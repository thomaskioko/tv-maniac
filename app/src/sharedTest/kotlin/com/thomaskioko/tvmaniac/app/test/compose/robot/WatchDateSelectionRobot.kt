package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseRobot
import com.thomaskioko.tvmaniac.testtags.watchdateselection.WatchDateSelectionTestTags

@OptIn(ExperimentalTestApi::class)
internal class WatchDateSelectionRobot(composeUi: ComposeUiTest) : BaseRobot<WatchDateSelectionRobot>(composeUi) {

    fun assertSheetDisplayed() = apply {
        awaitTagOnce(WatchDateSelectionTestTags.SHEET_TEST_TAG)
        assertDisplayed(WatchDateSelectionTestTags.SHEET_TEST_TAG)
        waitForIdle()
    }

    fun assertSheetDoesNotExist() = apply {
        assertDoesNotExist(WatchDateSelectionTestTags.SHEET_TEST_TAG)
    }

    fun assertReleaseDateDisplayed() = apply {
        assertDisplayed(WatchDateSelectionTestTags.RELEASE_DATE)
    }

    fun clickJustNow() = apply {
        click(WatchDateSelectionTestTags.JUST_NOW)
    }

    fun clickReleaseDate() = apply {
        click(WatchDateSelectionTestTags.RELEASE_DATE)
    }

    fun clickOtherDate() = apply {
        click(WatchDateSelectionTestTags.OTHER_DATE)
    }

    fun clickUnknownDate() = apply {
        click(WatchDateSelectionTestTags.UNKNOWN_DATE)
    }

    fun assertDatePickerDisplayed() = apply {
        awaitTagOnce(WatchDateSelectionTestTags.DATE_PICKER)
        assertDisplayed(WatchDateSelectionTestTags.DATE_PICKER)
    }

    fun confirmPicker() = apply {
        click(WatchDateSelectionTestTags.PICKER_CONFIRM)
    }

    fun cancelPicker() = apply {
        click(WatchDateSelectionTestTags.PICKER_CANCEL)
    }
}
