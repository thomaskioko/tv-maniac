package com.thomaskioko.tvmaniac.app.test.compose.flows.showdetails

import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import com.thomaskioko.tvmaniac.testtags.home.HomeTestTags
import org.junit.Test

internal class ShowDetailsRewatchFlowTest : BaseAppFlowTest() {

    private val breakingBadTmdbId = 1396L

    @Test
    fun givenFollowedShow_whenMoreOpened_thenWatchAgainIsOffered() = runAppFlowTest {
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
            .clickWatchAgain()
            .confirmWatchAgain()
    }

    @Test
    fun givenMultiplePlaysDisabled_whenMoreOpened_thenWatchAgainIsHidden() = runAppFlowTest {
        scenarios.stubAuthenticatedSync()
        scenarios.settings.disableMultiplePlays()

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
            .assertWatchAgainDoesNotExist()
    }
}
