package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseRobot
import com.thomaskioko.tvmaniac.testtags.discover.DiscoverTestTags

internal class DiscoverRobot(composeTestRule: ComposeContentTestRule) : BaseRobot(composeTestRule) {

    fun assertDiscoverScreenDisplayed() {
        assertDisplayed(DiscoverTestTags.SCREEN_TEST_TAG)
    }

    fun assertShowCardDisplayed(traktId: Long) {
        assertDisplayed(DiscoverTestTags.showCard(traktId))
    }

    fun assertUpNextCardDisplayed(traktId: Long) {
        assertDisplayed(DiscoverTestTags.upNextCard(traktId))
    }

    fun assertUpNextCardDoesNotExist(traktId: Long) {
        assertDoesNotExist(DiscoverTestTags.upNextCard(traktId))
    }

    fun assertFeaturedPagerDisplayed() {
        assertDisplayed(DiscoverTestTags.FEATURED_PAGER_TEST_TAG)
    }

    fun assertFeaturedShowDisplayed(traktId: Long) {
        assertDisplayed(DiscoverTestTags.featuredShowItem(traktId))
    }

    fun swipeFeaturedPagerLeft() {
        composeTestRule.onNodeWithTag(DiscoverTestTags.FEATURED_PAGER_TEST_TAG)
            .performTouchInput { swipeLeft() }
        composeTestRule.waitForIdle()
    }

    fun swipeFeaturedPagerRight() {
        composeTestRule.onNodeWithTag(DiscoverTestTags.FEATURED_PAGER_TEST_TAG)
            .performTouchInput { swipeRight() }
        composeTestRule.waitForIdle()
    }

    fun clickShowCard(traktId: Long) {
        click(DiscoverTestTags.showCard(traktId))
    }

    fun navigateToSearchTab(): SearchRobot {
        click(DiscoverTestTags.SEARCH_BUTTON_TEST_TAG, useSemanticsAction = true)
        return SearchRobot(composeTestRule)
    }

    fun clickUpNextCard(traktId: Long) {
        click(DiscoverTestTags.upNextCard(traktId), useSemanticsAction = true)
    }

    fun assertErrorStateDisplayed() {
        assertDisplayed(DiscoverTestTags.ERROR_RETRY_BUTTON_TEST_TAG)
    }

    fun clickRetryButton() {
        click(DiscoverTestTags.ERROR_RETRY_BUTTON_TEST_TAG, useSemanticsAction = true)
    }
}
