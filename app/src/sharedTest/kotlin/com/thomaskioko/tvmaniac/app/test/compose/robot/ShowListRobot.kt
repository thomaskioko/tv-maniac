package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseRobot
import com.thomaskioko.tvmaniac.testtags.showlist.ShowListTestTags

@OptIn(ExperimentalTestApi::class)
internal class ShowListRobot(composeUi: ComposeUiTest) : BaseRobot<ShowListRobot>(composeUi) {

    fun assertSheetDisplayed() = apply {
        awaitTagOnce(ShowListTestTags.SHEET_TEST_TAG, timeoutMillis = SHEET_APPEARANCE_TIMEOUT_MILLIS)
        assertDisplayed(ShowListTestTags.SHEET_TEST_TAG)
        waitForIdle()
    }

    fun assertSheetDoesNotExist() = apply {
        assertDoesNotExist(ShowListTestTags.SHEET_TEST_TAG)
    }

    fun assertLoginRequiredDisplayed() = apply {
        awaitTagOnce(
            ShowListTestTags.LOGIN_REQUIRED_CONFIRM_BUTTON_TEST_TAG,
            timeoutMillis = SHEET_APPEARANCE_TIMEOUT_MILLIS,
        )
        assertExists(ShowListTestTags.LOGIN_REQUIRED_CONFIRM_BUTTON_TEST_TAG)
    }

    fun assertLoginRequiredDoesNotExist() = apply {
        assertDoesNotExist(ShowListTestTags.LOGIN_REQUIRED_CONFIRM_BUTTON_TEST_TAG)
    }

    fun confirmLogin() = apply {
        click(ShowListTestTags.LOGIN_REQUIRED_CONFIRM_BUTTON_TEST_TAG)
    }

    fun assertTraktListItemDisplayed(listId: Long) = apply {
        assertExists(ShowListTestTags.traktListItem(listId))
    }

    fun clickCloseSheetButton() = apply {
        click(ShowListTestTags.CLOSE_BUTTON_TEST_TAG)
    }

    fun clickCreateListButton() = apply {
        click(ShowListTestTags.CREATE_LIST_BUTTON_TEST_TAG)
    }

    fun assertCreateListFieldDisplayed() = apply {
        assertExists(ShowListTestTags.CREATE_LIST_INPUT_TEST_TAG)
    }

    fun assertCreateListFieldDoesNotExist() = apply {
        assertDoesNotExist(ShowListTestTags.CREATE_LIST_INPUT_TEST_TAG)
    }

    fun typeCreateListName(name: String) = apply {
        inputText(ShowListTestTags.CREATE_LIST_INPUT_TEST_TAG, name)
    }

    fun clickCreateListSubmit() = apply {
        click(ShowListTestTags.CREATE_LIST_SUBMIT_TEST_TAG)
    }

    fun clickListSwitch(listId: Long) = apply {
        click(ShowListTestTags.traktListItemSwitch(listId))
    }

    fun assertListSwitchIsChecked(listId: Long) = apply {
        assertChecked(ShowListTestTags.traktListItemSwitch(listId))
    }

    fun assertListSwitchIsUnchecked(listId: Long) = apply {
        assertUnchecked(ShowListTestTags.traktListItemSwitch(listId))
    }

    fun assertTraktListShowCountText(listId: Long, expectedText: String) = apply {
        assertNodeHasText(ShowListTestTags.traktListItemShowCount(listId), expectedText)
    }

    private companion object {
        private const val SHEET_APPEARANCE_TIMEOUT_MILLIS: Long = 15_000
    }
}
