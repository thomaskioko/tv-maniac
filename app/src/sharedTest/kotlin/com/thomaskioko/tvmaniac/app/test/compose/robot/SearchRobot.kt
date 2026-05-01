package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithTag
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseRobot
import com.thomaskioko.tvmaniac.testing.integration.ui.TIMEOUT_MILLIS
import com.thomaskioko.tvmaniac.testing.integration.ui.replaceText
import com.thomaskioko.tvmaniac.testtags.search.SearchTestTags

internal class SearchRobot(composeTestRule: ComposeContentTestRule) : BaseRobot(composeTestRule) {

    fun assertSearchScreenDisplayed() {
        assertDisplayed(SearchTestTags.SCREEN_TEST_TAG)
    }

    fun enterSearchQuery(query: String) {
        composeTestRule.replaceText(
            tag = SearchTestTags.SEARCH_BAR_TEST_TAG,
            text = query,
            timeoutMillis = TIMEOUT_MILLIS,
        )
        composeTestRule.waitForIdle()
    }

    fun assertSearchQueryDisplayed(query: String) {
        composeTestRule.onNodeWithTag(SearchTestTags.SEARCH_BAR_TEST_TAG)
            .assertTextContains(query)
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
        return ShowDetailsRobot(composeTestRule)
    }
}
