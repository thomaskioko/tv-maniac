package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseRobot
import com.thomaskioko.tvmaniac.testtags.myshows.MyShowsTestTags
import com.thomaskioko.tvmaniac.testtags.startwatching.StartWatchingTestTags

@OptIn(ExperimentalTestApi::class)
internal class MyShowsRobot(composeUi: ComposeUiTest) : BaseRobot<MyShowsRobot>(composeUi) {

    fun assertMyShowsScreenDisplayed() = apply {
        assertDisplayed(MyShowsTestTags.SCREEN_TEST_TAG)
    }

    fun assertEmptyStateDisplayed() = apply {
        assertDisplayed(MyShowsTestTags.EMPTY_STATE_TEST_TAG)
    }

    fun assertGridDisplayed() = apply {
        assertDisplayed(MyShowsTestTags.MY_SHOWS_GRID_TEST_TAG)
    }

    fun assertListDisplayed() = apply {
        assertDisplayed(MyShowsTestTags.MY_SHOWS_LIST_TEST_TAG)
    }

    fun assertShowCardDisplayed(traktId: Long) = apply {
        assertExists(MyShowsTestTags.showCard(traktId))
    }

    fun scrollToShowCard(traktId: Long) = apply {
        val tag = MyShowsTestTags.showCard(traktId)
        scrollDownUntilTag(MyShowsTestTags.MY_SHOWS_GRID_TEST_TAG, tag)
    }

    fun assertShowCardDoesNotExist(traktId: Long) = apply {
        assertDoesNotExist(MyShowsTestTags.showCard(traktId))
    }

    fun clickShowCard(traktId: Long): ShowDetailsRobot {
        scrollToShowCard(traktId)
        click(MyShowsTestTags.showCard(traktId))
        return ShowDetailsRobot(composeUi)
    }

    fun enterSearchQuery(query: String) = apply {
        replaceText(tag = MyShowsTestTags.SEARCH_BAR_TEST_TAG, text = query)
    }

    fun clickSortButton() = apply {
        click(MyShowsTestTags.SORT_BUTTON_TEST_TAG)
    }

    fun assertSortSheetDisplayed() = apply {
        assertDisplayed(MyShowsTestTags.SORT_SHEET_TEST_TAG)
    }

    fun selectSortOption(label: String) = apply {
        clickText(label)
    }

    fun clickSearchButton() = apply {
        click(MyShowsTestTags.SEARCH_BUTTON_TEST_TAG)
    }

    fun clickLayoutMenuButton() = apply {
        click(MyShowsTestTags.LAYOUT_MENU_BUTTON_TEST_TAG)
    }

    fun assertLayoutMenuButtonDisplayed() = apply {
        assertDisplayed(MyShowsTestTags.LAYOUT_MENU_BUTTON_TEST_TAG)
    }

    fun assertLayoutMenuButtonDoesNotExist() = apply {
        assertDoesNotExist(MyShowsTestTags.LAYOUT_MENU_BUTTON_TEST_TAG)
    }

    fun assertLayoutMenuDisplayed() = apply {
        assertDisplayed(MyShowsTestTags.LAYOUT_MENU_TEST_TAG)
    }

    fun clickLayoutMenuItem(tag: String) = apply {
        click(tag)
    }

    fun assertLayoutMenuLockedSectionDisplayed() = apply {
        assertDisplayed(MyShowsTestTags.LAYOUT_MENU_LOCKED_SECTION_TEST_TAG)
    }

    fun assertCompactDisplayed() = apply {
        assertDisplayed(MyShowsTestTags.MY_SHOWS_COMPACT_TEST_TAG)
    }

    fun assertDetailedDisplayed() = apply {
        assertDisplayed(MyShowsTestTags.MY_SHOWS_DETAILED_TEST_TAG)
    }

    fun clickStartWatchingTab() = apply {
        click(MyShowsTestTags.START_WATCHING_TAB)
        waitForIdle()
    }

    fun swipeToStartWatching() = apply {
        swipeLeft(MyShowsTestTags.HORIZONTAL_PAGER)
        waitForIdle()
    }

    fun assertStartWatchingGridDisplayed() = apply {
        assertDisplayed(StartWatchingTestTags.GRID)
    }

    fun assertStartWatchingEmptyStateDisplayed() = apply {
        assertDisplayed(StartWatchingTestTags.EMPTY_STATE)
    }

    fun assertStartWatchingShowCardDisplayed(traktId: Long) = apply {
        assertExists(StartWatchingTestTags.showCard(traktId))
    }

    fun assertStartWatchingShowCardDoesNotExist(traktId: Long) = apply {
        assertDoesNotExist(StartWatchingTestTags.showCard(traktId))
    }

    fun scrollToStartWatchingShowCard(traktId: Long) = apply {
        scrollDownUntilTag(StartWatchingTestTags.GRID, StartWatchingTestTags.showCard(traktId))
    }

    fun clickStartWatchingShowCard(traktId: Long): ShowDetailsRobot {
        scrollToStartWatchingShowCard(traktId)
        click(StartWatchingTestTags.showCard(traktId))
        return ShowDetailsRobot(composeUi)
    }
}
