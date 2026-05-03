package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseRobot
import com.thomaskioko.tvmaniac.testtags.discover.DiscoverTestTags

@OptIn(ExperimentalTestApi::class)
internal class DiscoverRobot(composeUi: ComposeUiTest) : BaseRobot<DiscoverRobot>(composeUi) {

    fun assertDiscoverScreenDisplayed() = apply {
        assertDisplayed(DiscoverTestTags.SCREEN_TEST_TAG)
    }

    fun assertLoadingIndicatorDisplayed() = apply {
        assertDisplayed(DiscoverTestTags.PROGRESS_INDICATOR)
    }

    fun assertLoadingIndicatorDoesNotExist() = apply {
        assertDoesNotExist(DiscoverTestTags.PROGRESS_INDICATOR)
    }

    fun assertShowCardDisplayed(traktId: Long) = apply {
        val rowTag = DiscoverTestTags.ROW_KEY_TRENDING
        val cardTag = DiscoverTestTags.showCard(rowTag, traktId)

        assertFeaturedPagerDisplayed()
        scrollDownUntilTag(DiscoverTestTags.DISCOVER_LIST_TEST_TAG, rowTag)
        assertDisplayed(cardTag)
    }

    fun assertUpNextCardDisplayed(traktId: Long) = apply {
        val rowTag = DiscoverTestTags.UP_NEXT_SECTION_TEST_TAG

        assertFeaturedPagerDisplayed()
        scrollDownUntilTag(DiscoverTestTags.DISCOVER_LIST_TEST_TAG, rowTag)
        assertDisplayed(DiscoverTestTags.upNextCard(traktId))
    }

    fun assertUpNextCardDoesNotExist(traktId: Long) = apply {
        assertDoesNotExist(DiscoverTestTags.upNextCard(traktId))
    }

    fun assertFeaturedPagerDisplayed() = apply {
        assertDisplayed(DiscoverTestTags.FEATURED_PAGER_TEST_TAG)
    }

    fun assertFeaturedShowDisplayed(traktId: Long) = apply {
        assertDisplayed(DiscoverTestTags.featuredShowItem(traktId))
    }

    fun swipeFeaturedPagerLeft() = apply {
        swipeLeft(DiscoverTestTags.FEATURED_PAGER_TEST_TAG)
        waitForIdle()
    }

    fun swipeFeaturedPagerRight() = apply {
        swipeRight(DiscoverTestTags.FEATURED_PAGER_TEST_TAG)
        waitForIdle()
    }

    fun clickShowCard(traktId: Long): ShowDetailsRobot {
        val rowTag = DiscoverTestTags.ROW_KEY_TRENDING
        val cardTag = DiscoverTestTags.showCard(rowTag, traktId)

        // Use swipe-based scroll down for better reliability
        scrollDownUntilTag(DiscoverTestTags.DISCOVER_LIST_TEST_TAG, rowTag)

        // Now click the card (will wait for it to be visible/composed)
        click(cardTag)
        return ShowDetailsRobot(composeUi)
    }

    fun navigateToSearchTab(): SearchRobot {
        click(DiscoverTestTags.SEARCH_BUTTON_TEST_TAG)
        return SearchRobot(composeUi)
    }

    fun clickUpNextCard(traktId: Long): EpisodeSheetRobot {
        val rowTag = DiscoverTestTags.UP_NEXT_SECTION_TEST_TAG

        scrollDownUntilTag(DiscoverTestTags.DISCOVER_LIST_TEST_TAG, rowTag)
        click(DiscoverTestTags.upNextCard(traktId))
        return EpisodeSheetRobot(composeUi)
    }

    fun assertErrorStateDisplayed() = apply {
        assertDisplayed(DiscoverTestTags.ERROR_RETRY_BUTTON_TEST_TAG)
    }

    fun clickRetryButton() = apply {
        click(DiscoverTestTags.ERROR_RETRY_BUTTON_TEST_TAG)
    }
}
