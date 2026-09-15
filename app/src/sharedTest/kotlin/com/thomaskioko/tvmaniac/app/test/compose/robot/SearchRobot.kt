package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.performImeAction
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

    fun pressImeSearchAction() = apply {
        awaitTagOnce(SearchTestTags.SEARCH_BAR_TEST_TAG)
        composeUi.onNode(hasTestTag(SearchTestTags.SEARCH_BAR_TEST_TAG)).performImeAction()
        waitForIdle()
    }

    fun assertSearchQueryDisplayed(query: String) = apply {
        assertTextContains(SearchTestTags.SEARCH_BAR_TEST_TAG, query)
    }

    fun assertResultCountEquals(count: Int) = apply {
        waitForIdle()
        val resultItemPrefix = "${SearchTestTags.RESULT_ITEM_TEST_TAG}_"
        composeUi.onAllNodes(
            matcher = SemanticsMatcher("testTag starts with $resultItemPrefix") { node ->
                node.config.getOrNull(SemanticsProperties.TestTag)?.startsWith(resultItemPrefix) == true
            },
        ).assertCountEquals(count)
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
