package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseRobot
import com.thomaskioko.tvmaniac.testtags.library.LibraryTestTags

@OptIn(ExperimentalTestApi::class)
internal class LibraryRobot(composeUi: ComposeUiTest) : BaseRobot<LibraryRobot>(composeUi) {

    fun assertLibraryScreenDisplayed() = apply {
        assertDisplayed(LibraryTestTags.SCREEN_TEST_TAG)
    }

    fun assertEmptyStateDisplayed() = apply {
        assertDisplayed(LibraryTestTags.EMPTY_STATE_TEST_TAG)
    }

    fun assertShowRowDisplayed(traktId: Long) = apply {
        assertExists(LibraryTestTags.showRow(traktId))
    }

    fun scrollToShowRow(traktId: Long) = apply {
        val tag = LibraryTestTags.showRow(traktId)
        scrollDownUntilTag(LibraryTestTags.LIBRARY_LIST_TEST_TAG, tag)
    }

    fun assertShowRowDoesNotExist(traktId: Long) = apply {
        assertDoesNotExist(LibraryTestTags.showRow(traktId))
    }

    fun clickShowRow(traktId: Long): ShowDetailsRobot {
        scrollToShowRow(traktId)
        click(LibraryTestTags.showRow(traktId))
        return ShowDetailsRobot(composeUi)
    }

    fun clickSearchButton() = apply {
        click(LibraryTestTags.SEARCH_BUTTON_TEST_TAG)
    }

    fun enterSearchQuery(query: String) = apply {
        replaceText(tag = LibraryTestTags.SEARCH_BAR_TEST_TAG, text = query)
    }

    fun clickFilterButton() = apply {
        click(LibraryTestTags.FILTER_BUTTON_TEST_TAG)
    }

    fun clickApplyFilter() = apply {
        click(LibraryTestTags.APPLY_FILTER_BUTTON_TEST_TAG)
    }

    fun clickClearFilter() = apply {
        click(LibraryTestTags.CLEAR_FILTER_BUTTON_TEST_TAG)
    }

    fun selectSortOption(text: String) = apply {
        clickText(text)
    }

    fun selectGenreFilter(text: String) = apply {
        clickText(text)
    }
}
