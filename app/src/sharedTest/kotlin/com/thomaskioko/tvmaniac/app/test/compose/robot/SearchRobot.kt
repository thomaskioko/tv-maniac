package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseRobot
import com.thomaskioko.tvmaniac.testtags.search.SearchTestTags

@OptIn(ExperimentalTestApi::class)
internal class SearchRobot(composeUi: ComposeUiTest) : BaseRobot(composeUi) {

    fun assertSearchScreenDisplayed() {
        assertDisplayed(SearchTestTags.SCREEN_TEST_TAG)
    }

    fun enterSearchQuery(query: String) {
        replaceText(tag = SearchTestTags.SEARCH_BAR_TEST_TAG, text = query)
        waitForIdle()
    }

    fun assertSearchQueryDisplayed(query: String) {
        assertTextContains(SearchTestTags.SEARCH_BAR_TEST_TAG, query, useUnmergedTree = false)
    }

    fun assertResultCountEquals(count: Int) {
        assertCountEquals(
            SearchTestTags.SCREEN_TEST_TAG,
            "search_result_item_",
            count,
            useUnmergedTree = true,
        )
    }

    fun assertResultItemDisplayed(traktId: Long) {
        assertDisplayed(SearchTestTags.resultItem(traktId), useUnmergedTree = true)
    }

    fun assertResultTitleDisplayed(title: String) {
        assertTextDisplayed(title)
    }

    fun assertEmptyStateDisplayed() {
        assertDisplayed(SearchTestTags.EMPTY_STATE_TEST_TAG)
    }

    fun assertErrorStateDisplayed() {
        assertDisplayed(SearchTestTags.ERROR_STATE_TEST_TAG)
    }

    fun clickResultItem(traktId: Long): ShowDetailsRobot {
        click(SearchTestTags.resultItem(traktId))
        return ShowDetailsRobot(composeUi)
    }
}
