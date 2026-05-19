package com.thomaskioko.tvmaniac.app.test.compose.flows.upnext

import com.thomaskioko.tvmaniac.app.test.AppFlowScope
import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import com.thomaskioko.tvmaniac.testtags.home.HomeTestTags
import org.junit.Test

internal class UpNextFlowTests : BaseAppFlowTest() {

    private val breakingBadTraktId = 1388L
    private val pilotEpisodeTraktId = 73640L

    @Test
    fun upNextUserJourney() = runAppFlowTest {
        scenarios.stubAuthenticatedSync()

        navigateToUpNext()

        // 1. Verify Episode Row & Meta
        progressRobot
            .assertUpNextTabSelected()
            .assertUpNextPageDisplayed()
            .scrollToUpNextEpisode(breakingBadTraktId)
            .assertUpNextEpisodeDisplayed(breakingBadTraktId)
            .assertUpNextEpisodeMetaDisplayed(breakingBadTraktId, "S01E01")
            .assertUpNextProgressCountDisplayed(breakingBadTraktId, "0/9")
            .clickUpNextEpisodeRow(breakingBadTraktId)
            .assertSeasonDetailsDisplayed()
            .clickBackButton()

        // 3. Mark Watched & Verify advancement
        scenarios.upNext.stubProgressAfterPilotWatched(breakingBadTraktId)

        progressRobot
            .scrollToUpNextEpisode(breakingBadTraktId)
            .clickUpNextWatchedButton(breakingBadTraktId)
            .assertUpNextEpisodeMetaDisplayed(breakingBadTraktId, "S01E02")
            .assertUpNextProgressCountDisplayed(breakingBadTraktId, "1/9")
            // 4. Verify in Season Details
            .clickUpNextEpisodeRow(breakingBadTraktId)
            .assertSeasonDetailsDisplayed()
            .assertMarkUnwatchedDisplayed(pilotEpisodeTraktId)
    }

    private fun AppFlowScope.navigateToUpNext() {
        rootRobot
            .dismissNotificationRationale()

        discoverRobot
            .assertDiscoverScreenDisplayed()

        homeRobot
            .clickWatchlistTab()
            .assertTabSelected(HomeTestTags.WATCHLIST_TAB)

        watchlistRobot
            .scrollToShowCard(breakingBadTraktId)
            .assertShowCardDisplayed(breakingBadTraktId)

        homeRobot
            .clickProgressTab()
            .assertTabSelected(HomeTestTags.PROGRESS_TAB)
    }
}
