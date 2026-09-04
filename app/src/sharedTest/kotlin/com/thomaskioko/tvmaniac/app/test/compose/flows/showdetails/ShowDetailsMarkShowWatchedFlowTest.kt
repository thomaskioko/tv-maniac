package com.thomaskioko.tvmaniac.app.test.compose.flows.showdetails

import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import com.thomaskioko.tvmaniac.testtags.home.HomeTestTags
import org.junit.Test

internal class ShowDetailsMarkShowWatchedFlowTest : BaseAppFlowTest() {

    private val breakingBadTmdbId = 1396L

    @Test
    fun givenFollowedShow_whenMarkShowWatchedConfirmed_thenWatchDateSelectionOpens() = runAppFlowTest {
        scenarios.settings.enableCustomWatchDate()
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
            .clickMarkShowWatched()
            .confirmMarkShowWatched()

        watchDateSelectionRobot
            .assertSheetDisplayed()
    }
}
