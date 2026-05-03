package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseRobot
import com.thomaskioko.tvmaniac.testtags.search.SearchTestTags

@OptIn(ExperimentalTestApi::class)
internal class SearchRobot(composeUi: ComposeUiTest) : BaseRobot<SearchRobot>(composeUi) {

    fun assertSearchScreenDisplayed() = apply {
        assertDisplayed(SearchTestTags.SCREEN_TEST_TAG)
    }

    fun enterSearchQuery(query: String) = apply {
        replaceText(tag = SearchTestTags.SEARCH_BAR_TEST_TAG, text = query)
        waitForIdle()
    }

    fun assertSearchQueryDisplayed(query: String) = apply {
        assertTextContains(SearchTestTags.SEARCH_BAR_TEST_TAG, query)
    }

    fun assertResultCountEquals(count: Int) = apply {
        assertCountEquals(
            SearchTestTags.SCREEN_TEST_TAG,
            "search_result_item_",
            count,
        )
    }

    fun assertResultItemDisplayed(traktId: Long) = apply {
        assertDisplayed(SearchTestTags.resultItem(traktId))
    }

    fun assertResultTitleDisplayed(title: String) = apply {
        assertTextDisplayed(title)
    }

    fun assertEmptyStateDisplayed() = apply {
        assertDisplayed(SearchTestTags.EMPTY_STATE_TEST_TAG)
    }

    fun assertErrorStateDisplayed() = apply {
        assertDisplayed(SearchTestTags.ERROR_STATE_TEST_TAG)
    }

    fun clickResultItem(traktId: Long): ShowDetailsRobot {
        click(SearchTestTags.resultItem(traktId))
        return ShowDetailsRobot(composeUi)
    }
}
