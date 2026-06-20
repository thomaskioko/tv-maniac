package com.thomaskioko.tvmaniac.app.test.compose.flows.myshows

import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import com.thomaskioko.tvmaniac.testtags.home.HomeTestTags
import org.junit.Test

internal class StartWatchingFlowTest : BaseAppFlowTest() {

    private val unstartedTraktId = 555555L
    private val breakingBadTmdbId = 1396L
    private val forAllMankindTmdbId = 87917L
    private val theBoysTmdbId = 76479L

    @Test
    fun givenAuthenticatedUser_whenStartWatchingTabOpened_thenShowsUnstartedShowAndExcludesContinueWatching() =
        runAppFlowTest {
            scenarios.stubAuthenticatedSync()

            rootRobot.dismissNotificationRationale()

            discoverRobot.assertDiscoverScreenDisplayed()

            homeRobot
                .clickMyShowsTab()
                .assertTabSelected(HomeTestTags.MY_SHOWS_TAB)

            watchlistRobot
                .assertMyShowsScreenDisplayed()
                .clickStartWatchingTab()
                .assertStartWatchingGridDisplayed()
                .scrollToStartWatchingShowCard(unstartedTraktId)
                .assertStartWatchingShowCardDisplayed(unstartedTraktId)
                .assertStartWatchingShowCardDoesNotExist(breakingBadTmdbId)
                .assertStartWatchingShowCardDoesNotExist(forAllMankindTmdbId)
                .assertStartWatchingShowCardDoesNotExist(theBoysTmdbId)

            rootRobot.assertNoSnackbarDisplayed()
        }

    @Test
    fun givenStartWatchingShowCard_whenClicked_thenNavigatesToShowDetails() = runAppFlowTest {
        scenarios.stubAuthenticatedSync()

        rootRobot.dismissNotificationRationale()

        discoverRobot.assertDiscoverScreenDisplayed()

        homeRobot
            .clickMyShowsTab()
            .assertTabSelected(HomeTestTags.MY_SHOWS_TAB)

        watchlistRobot
            .clickStartWatchingTab()
            .clickStartWatchingShowCard(unstartedTraktId)
            .assertShowDetailsDisplayed()
    }

    @Test
    fun givenStartWatchingTabSelected_thenListStyleToggleHidden() = runAppFlowTest {
        scenarios.stubAuthenticatedSync()

        rootRobot.dismissNotificationRationale()

        discoverRobot.assertDiscoverScreenDisplayed()

        homeRobot
            .clickMyShowsTab()
            .assertTabSelected(HomeTestTags.MY_SHOWS_TAB)

        watchlistRobot
            .assertListStyleToggleDisplayed()
            .clickStartWatchingTab()
            .assertStartWatchingGridDisplayed()
            .assertListStyleToggleDoesNotExist()
    }

    @Test
    fun givenContinueWatchingTab_whenSwipedLeft_thenShowsStartWatchingGrid() = runAppFlowTest {
        scenarios.stubAuthenticatedSync()

        rootRobot.dismissNotificationRationale()

        discoverRobot.assertDiscoverScreenDisplayed()

        homeRobot
            .clickMyShowsTab()
            .assertTabSelected(HomeTestTags.MY_SHOWS_TAB)

        watchlistRobot
            .assertGridDisplayed()
            .swipeToStartWatching()
            .assertStartWatchingGridDisplayed()
            .assertStartWatchingShowCardDisplayed(unstartedTraktId)
    }

    @Test
    fun givenUnauthenticatedUser_whenStartWatchingTabOpened_thenShowsEmptyState() = runAppFlowTest {
        scenarios.stubUnauthenticatedJourney()

        discoverRobot.assertDiscoverScreenDisplayed()

        homeRobot
            .clickMyShowsTab()
            .assertTabSelected(HomeTestTags.MY_SHOWS_TAB)

        watchlistRobot
            .clickStartWatchingTab()
            .assertStartWatchingEmptyStateDisplayed()
            .assertStartWatchingShowCardDoesNotExist(unstartedTraktId)
    }
}
