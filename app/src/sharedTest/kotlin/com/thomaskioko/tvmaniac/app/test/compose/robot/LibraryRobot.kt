package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseRobot
import com.thomaskioko.tvmaniac.testtags.library.LibraryTestTags

@OptIn(ExperimentalTestApi::class)
internal class LibraryRobot(composeUi: ComposeUiTest) : BaseRobot(composeUi) {

    fun assertLibraryScreenDisplayed() {
        assertDisplayed(LibraryTestTags.SCREEN_TEST_TAG)
    }

    fun assertEmptyStateDisplayed() {
        assertDisplayed(LibraryTestTags.EMPTY_STATE_TEST_TAG)
    }

    fun assertShowRowDisplayed(traktId: Long) {
        assertExists(LibraryTestTags.showRow(traktId))
    }

    fun assertShowRowDoesNotExist(traktId: Long) {
        assertDoesNotExist(LibraryTestTags.showRow(traktId))
    }

    fun clickShowRow(traktId: Long) {
        click(LibraryTestTags.showRow(traktId))
    }

    fun clickSearchButton() {
        click(LibraryTestTags.SEARCH_BUTTON_TEST_TAG)
    }

    fun enterSearchQuery(query: String) {
        replaceText(tag = LibraryTestTags.SEARCH_BAR_TEST_TAG, text = query)
    }

    fun clickFilterButton() {
        click(LibraryTestTags.FILTER_BUTTON_TEST_TAG)
    }

    fun clickApplyFilter() {
        click(LibraryTestTags.APPLY_FILTER_BUTTON_TEST_TAG)
    }

    fun clickClearFilter() {
        click(LibraryTestTags.CLEAR_FILTER_BUTTON_TEST_TAG)
    }

    fun selectSortOption(text: String) {
        clickText(text)
    }

    fun selectGenreFilter(text: String) {
        clickText(text)
    }
}
