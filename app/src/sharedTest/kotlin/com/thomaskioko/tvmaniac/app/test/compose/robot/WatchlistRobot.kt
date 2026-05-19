package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseRobot
import com.thomaskioko.tvmaniac.testtags.watchlist.WatchlistTestTags

@OptIn(ExperimentalTestApi::class)
internal class WatchlistRobot(composeUi: ComposeUiTest) : BaseRobot<WatchlistRobot>(composeUi) {

    fun assertWatchlistScreenDisplayed() = apply {
        assertDisplayed(WatchlistTestTags.SCREEN_TEST_TAG)
    }

    fun assertEmptyStateDisplayed() = apply {
        assertDisplayed(WatchlistTestTags.EMPTY_STATE_TEST_TAG)
    }

    fun assertGridDisplayed() = apply {
        assertDisplayed(WatchlistTestTags.WATCHLIST_GRID_TEST_TAG)
    }

    fun assertListDisplayed() = apply {
        assertDisplayed(WatchlistTestTags.WATCHLIST_LIST_TEST_TAG)
    }

    fun assertShowCardDisplayed(traktId: Long) = apply {
        assertExists(WatchlistTestTags.showCard(traktId))
    }

    fun scrollToShowCard(traktId: Long) = apply {
        val tag = WatchlistTestTags.showCard(traktId)
        scrollDownUntilTag(WatchlistTestTags.WATCHLIST_GRID_TEST_TAG, tag)
    }

    fun assertShowCardDoesNotExist(traktId: Long) = apply {
        assertDoesNotExist(WatchlistTestTags.showCard(traktId))
    }

    fun clickShowCard(traktId: Long): ShowDetailsRobot {
        scrollToShowCard(traktId)
        click(WatchlistTestTags.showCard(traktId))
        return ShowDetailsRobot(composeUi)
    }

    fun enterSearchQuery(query: String) = apply {
        replaceText(tag = WatchlistTestTags.SEARCH_BAR_TEST_TAG, text = query)
    }

    fun clickSortButton() = apply {
        click(WatchlistTestTags.SORT_BUTTON_TEST_TAG)
    }

    fun assertSortSheetDisplayed() = apply {
        assertDisplayed(WatchlistTestTags.SORT_SHEET_TEST_TAG)
    }

    fun selectSortOption(label: String) = apply {
        clickText(label)
    }

    fun clickSearchButton() = apply {
        click(WatchlistTestTags.SEARCH_BUTTON_TEST_TAG)
    }

    fun clickToggleListStyleButton() = apply {
        click(WatchlistTestTags.TOGGLE_LIST_STYLE_BUTTON_TEST_TAG)
    }
}
