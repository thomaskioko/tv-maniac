package com.thomaskioko.tvmaniac.app.test.compose.flows.upnext

import com.thomaskioko.tvmaniac.app.test.AppFlowScope
import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import com.thomaskioko.tvmaniac.testtags.home.HomeTestTags
import org.junit.Test

internal class UpNextFlowTests : BaseAppFlowTest() {

    private val breakingBadTmdbId = 1396L
    private val pilotEpisodeTraktId = 73640L

    @Test
    fun upNextUserJourney() = runAppFlowTest {
        scenarios.stubAuthenticatedSync()

        navigateToUpNext()

        // 1. Verify Episode Row & Meta
        progressRobot
            .assertUpNextTabSelected()
            .assertUpNextPageDisplayed()
            .scrollToUpNextEpisode(breakingBadTmdbId)
            .assertUpNextEpisodeDisplayed(breakingBadTmdbId)
            .assertUpNextEpisodeMetaDisplayed(breakingBadTmdbId, "S01E01")
            .assertUpNextProgressCountDisplayed(breakingBadTmdbId, "0/9")
            .clickUpNextEpisodeRow(breakingBadTmdbId)
            .assertSeasonDetailsDisplayed()
            .clickBackButton()

        // 3. Mark Watched & Verify advancement
        scenarios.upNext.stubProgressAfterPilotWatched(breakingBadTmdbId)

        progressRobot
            .scrollToUpNextEpisode(breakingBadTmdbId)
            .clickUpNextWatchedButton(breakingBadTmdbId)
            .assertUpNextEpisodeMetaDisplayed(breakingBadTmdbId, "S01E02")
            .assertUpNextProgressCountDisplayed(breakingBadTmdbId, "1/9")
            // 4. Verify in Season Details
            .clickUpNextEpisodeRow(breakingBadTmdbId)
            .assertSeasonDetailsDisplayed()
            .assertMarkUnwatchedDisplayed(pilotEpisodeTraktId)
    }

    private fun AppFlowScope.navigateToUpNext() {
        rootRobot
            .dismissNotificationRationale()

        discoverRobot
            .assertDiscoverScreenDisplayed()

        homeRobot
            .clickMyShowsTab()
            .assertTabSelected(HomeTestTags.MY_SHOWS_TAB)

        watchlistRobot
            .scrollToShowCard(breakingBadTmdbId)
            .assertShowCardDisplayed(breakingBadTmdbId)

        homeRobot
            .clickProgressTab()
            .assertTabSelected(HomeTestTags.PROGRESS_TAB)
    }
}
