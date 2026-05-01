package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithTag
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseRobot
import com.thomaskioko.tvmaniac.testing.integration.ui.replaceText
import com.thomaskioko.tvmaniac.testtags.search.SearchTestTags

internal class SearchRobot(composeTestRule: ComposeContentTestRule) : BaseRobot(composeTestRule) {

    fun verifySearchScreenIsShown() {
        verifyTagShown(SearchTestTags.SCREEN_TEST_TAG)
    }

    fun enterSearchQuery(query: String) {
        composeTestRule.replaceText(SearchTestTags.SEARCH_BAR_TEST_TAG, query)
        composeTestRule.waitForIdle()
    }

    fun verifySearchQuery(query: String) {
        composeTestRule.onNodeWithTag(SearchTestTags.SEARCH_BAR_TEST_TAG)
            .assertTextContains(query)
    }

    fun verifyResultCount(count: Int) {
        verifyCount(SearchTestTags.SCREEN_TEST_TAG, "search_result_item_", count, useUnmergedTree = true)
    }

    fun verifyResultItemIsShown(traktId: Long) {
        verifyTagShown(SearchTestTags.resultItem(traktId), useUnmergedTree = true)
    }

    fun verifyResultTitleIsShown(title: String) {
        verifyTextShown(title)
    }

    fun verifyEmptyStateIsShown() {
        verifyTagShown(SearchTestTags.EMPTY_STATE_TEST_TAG)
    }

    fun verifyErrorStateIsShown() {
        verifyTagShown(SearchTestTags.ERROR_STATE_TEST_TAG)
    }

    fun clickResultItem(traktId: Long): ShowDetailsRobot {
        click(SearchTestTags.resultItem(traktId))
        return ShowDetailsRobot(composeTestRule)
    }
}
