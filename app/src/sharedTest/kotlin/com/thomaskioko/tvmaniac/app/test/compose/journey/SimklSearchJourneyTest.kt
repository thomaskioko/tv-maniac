package com.thomaskioko.tvmaniac.app.test.compose.journey

import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import org.junit.Test

internal class SimklSearchJourneyTest : BaseAppFlowTest() {

    private val breakingBadTmdbId = 1396L

    @Test
    fun givenSimklSession_whenSearching_thenFetchesFromSimklAndOpensShowDetails() = runAppFlowTest {
        scenarios.stubUnauthenticatedState()
        scenarios.stubAuthenticatedSimklStartWatching()
        scenarios.search.stubSimklSearch()

        val query = "Breaking Bad"

        discoverRobot
            .assertDiscoverScreenDisplayed()
            .navigateToSearchTab()

        searchRobot
            .assertSearchScreenDisplayed()
            .enterSearchQuery(query)
            .assertResultItemDisplayed(breakingBadTmdbId)
            .assertResultTitleDisplayed("Breaking Bad")
            .clickResultItem(breakingBadTmdbId)
            .assertShowDetailsDisplayed()
    }
}
