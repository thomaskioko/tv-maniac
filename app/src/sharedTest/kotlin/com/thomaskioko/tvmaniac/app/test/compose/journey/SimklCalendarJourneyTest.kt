package com.thomaskioko.tvmaniac.app.test.compose.journey

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import com.thomaskioko.tvmaniac.testtags.home.HomeTestTags
import org.junit.Test

internal class SimklCalendarJourneyTest : BaseAppFlowTest() {

    private val simklTmdbId = 778899L

    @Test
    fun givenSimklSession_whenCalendarOpened_thenShowsTrackedShowEpisode() = runAppFlowTest {
        scenarios.stubUnauthenticatedState()
        scenarios.stubActiveProvider(SyncProviderSource.SIMKL)
        scenarios.simkl.stubPlanToWatchWatchlist()

        homeRobot
            .clickMyShowsTab()
            .assertTabSelected(HomeTestTags.MY_SHOWS_TAB)

        watchlistRobot
            .assertMyShowsScreenDisplayed()
            .clickStartWatchingTab()
            .assertStartWatchingGridDisplayed()
            .scrollToStartWatchingShowCard(simklTmdbId)
            .assertStartWatchingShowCardDisplayed(simklTmdbId)

        homeRobot
            .clickProgressTab()
            .assertTabSelected(HomeTestTags.PROGRESS_TAB)

        progressRobot
            .assertProgressScreenDisplayed()
            .clickCalendarTab()
            .assertCalendarTabSelected()

        calendarRobot
            .assertCalendarScreenDisplayed()
            .assertTextDisplayed("Simkl Plan To Watch")
    }
}
