package com.thomaskioko.tvmaniac.app.test.compose.journey

import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import com.thomaskioko.tvmaniac.testtags.home.HomeTestTags
import org.junit.Test

internal class SimklStartWatchingJourneyTest : BaseAppFlowTest() {

    private val simklPlanToWatchTmdbId = 778899L

    @Test
    fun givenSimklSession_whenStartWatchingTabOpened_thenShowsPlanToWatchShow() = runAppFlowTest {
        scenarios.stubUnauthenticatedState()
        scenarios.stubAuthenticatedSimklStartWatching()

        homeRobot
            .clickMyShowsTab()
            .assertTabSelected(HomeTestTags.MY_SHOWS_TAB)

        watchlistRobot
            .assertMyShowsScreenDisplayed()
            .clickStartWatchingTab()
            .assertStartWatchingGridDisplayed()
            .scrollToStartWatchingShowCard(simklPlanToWatchTmdbId)
            .assertStartWatchingShowCardDisplayed(simklPlanToWatchTmdbId)
    }
}
