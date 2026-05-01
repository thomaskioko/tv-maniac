package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseRobot
import com.thomaskioko.tvmaniac.testing.integration.ui.replaceText
import com.thomaskioko.tvmaniac.testtags.library.LibraryTestTags

internal class LibraryRobot(composeTestRule: ComposeContentTestRule) : BaseRobot(composeTestRule) {

    fun verifyLibraryScreenIsShown() {
        verifyTagShown(LibraryTestTags.SCREEN_TEST_TAG)
    }

    fun verifyEmptyStateIsShown() {
        verifyTagShown(LibraryTestTags.EMPTY_STATE_TEST_TAG)
    }

    fun verifyShowRowIsShown(traktId: Long) {
        verifyTagExists(LibraryTestTags.showRow(traktId))
    }

    fun verifyShowRowIsHidden(traktId: Long) {
        verifyTagHidden(LibraryTestTags.showRow(traktId))
    }

    fun clickShowRow(traktId: Long) {
        click(LibraryTestTags.showRow(traktId), useSemanticsAction = true)
    }

    fun clickSearchButton() {
        click(LibraryTestTags.SEARCH_BUTTON_TEST_TAG, useSemanticsAction = true)
    }

    fun enterSearchQuery(query: String) {
        composeTestRule.replaceText(LibraryTestTags.SEARCH_BAR_TEST_TAG, query)
    }

    fun clickFilterButton() {
        click(LibraryTestTags.FILTER_BUTTON_TEST_TAG, useSemanticsAction = true)
    }

    fun clickApplyFilter() {
        click(LibraryTestTags.APPLY_FILTER_BUTTON_TEST_TAG, useSemanticsAction = true)
    }

    fun clickClearFilter() {
        click(LibraryTestTags.CLEAR_FILTER_BUTTON_TEST_TAG, useSemanticsAction = true)
    }

    fun selectSortOption(text: String) {
        clickText(text)
    }

    fun selectGenreFilter(text: String) {
        clickText(text)
    }
}
