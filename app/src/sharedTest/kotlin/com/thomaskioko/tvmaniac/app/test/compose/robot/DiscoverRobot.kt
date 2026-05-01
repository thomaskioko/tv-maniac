package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseRobot
import com.thomaskioko.tvmaniac.testtags.discover.DiscoverTestTags

@OptIn(ExperimentalTestApi::class)
internal class DiscoverRobot(composeUi: ComposeUiTest) : BaseRobot(composeUi) {

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
        swipeLeft(DiscoverTestTags.FEATURED_PAGER_TEST_TAG)
        waitForIdle()
    }

    fun swipeFeaturedPagerRight() {
        swipeRight(DiscoverTestTags.FEATURED_PAGER_TEST_TAG)
        waitForIdle()
    }

    fun clickShowCard(traktId: Long) {
        click(DiscoverTestTags.showCard(traktId))
    }

    fun navigateToSearchTab(): SearchRobot {
        click(DiscoverTestTags.SEARCH_BUTTON_TEST_TAG, useSemanticsAction = true)
        return SearchRobot(composeUi)
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
