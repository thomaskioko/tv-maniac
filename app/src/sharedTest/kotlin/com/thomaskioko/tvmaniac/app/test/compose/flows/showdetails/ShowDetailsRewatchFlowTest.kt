package com.thomaskioko.tvmaniac.app.test.compose.flows.showdetails

import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import com.thomaskioko.tvmaniac.testtags.home.HomeTestTags
import org.junit.Test

internal class ShowDetailsRewatchFlowTest : BaseAppFlowTest() {

    private val breakingBadTmdbId = 1396L

    @Test
    fun givenFollowedShow_whenShowDetailsOpened_thenRewatchSectionShowsNoRewatches() = runAppFlowTest {
        scenarios.stubAuthenticatedSync()

        rootRobot
            .dismissNotificationRationale()

        discoverRobot
            .assertDiscoverScreenDisplayed()

        homeRobot
            .clickMyShowsTab()
            .assertTabSelected(HomeTestTags.MY_SHOWS_TAB)

        watchlistRobot
            .clickShowCard(breakingBadTmdbId)
            .assertShowDetailsDisplayed()
            .assertRewatchSectionDisplayed()
            .assertRewatchCountText(NOT_REWATCHED_YET)
            .assertRewatchProgressDoesNotExist()
    }

    @Test
    fun givenNoRewatchUnderWay_whenRewatchStarted_thenSectionShowsProgress() = runAppFlowTest {
        scenarios.stubAuthenticatedSync()

        rootRobot
            .dismissNotificationRationale()

        discoverRobot
            .assertDiscoverScreenDisplayed()

        homeRobot
            .clickMyShowsTab()
            .assertTabSelected(HomeTestTags.MY_SHOWS_TAB)

        watchlistRobot
            .clickShowCard(breakingBadTmdbId)
            .assertShowDetailsDisplayed()
            .clickRewatchActionButton()
            .assertRewatchCountText(REWATCH_IN_PROGRESS)
            .assertRewatchProgressDisplayed()
    }

    @Test
    fun givenRewatchUnderWay_whenRewatchFinished_thenCountRises() = runAppFlowTest {
        scenarios.stubAuthenticatedSync()

        rootRobot
            .dismissNotificationRationale()

        discoverRobot
            .assertDiscoverScreenDisplayed()

        homeRobot
            .clickMyShowsTab()
            .assertTabSelected(HomeTestTags.MY_SHOWS_TAB)

        watchlistRobot
            .clickShowCard(breakingBadTmdbId)
            .assertShowDetailsDisplayed()
            .clickRewatchActionButton()
            .assertRewatchCountText(REWATCH_IN_PROGRESS)
            .clickRewatchActionButton()
            .assertRewatchCountText(REWATCHED_ONCE)
            .assertRewatchProgressDoesNotExist()
    }

    private companion object {
        private const val NOT_REWATCHED_YET = "Not rewatched yet"
        private const val REWATCH_IN_PROGRESS = "Rewatch in progress"
        private const val REWATCHED_ONCE = "Rewatched 1 time"
    }
}
