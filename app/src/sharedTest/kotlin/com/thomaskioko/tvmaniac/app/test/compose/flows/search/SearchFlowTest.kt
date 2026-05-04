package com.thomaskioko.tvmaniac.app.test.compose.flows.search

import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import org.junit.Test

internal class SearchFlowTest : BaseAppFlowTest() {

    @Test
    fun searchUserJourney() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        val query = "Breaking Bad"
        val traktId = 1388L

        discoverRobot
            .assertDiscoverScreenDisplayed()
            .navigateToSearchTab()

        scenarios.search.stubSearch(query)

        // 1. Enter Query & Verify results
        searchRobot
            .assertSearchScreenDisplayed()
            .enterSearchQuery(query)
            .assertSearchQueryDisplayed(query)
            .assertResultItemDisplayed(traktId)
            .assertResultTitleDisplayed("Breaking Bad")
            // 2. Click Result -> Show Details
            .clickResultItem(traktId)
            .assertShowDetailsDisplayed()
            .pressBack()

        // 3. Back -> Search Screen restored
        searchRobot
            .assertSearchScreenDisplayed()
            .assertResultItemDisplayed(traktId)
    }

    @Test
    fun givenSearch_whenSearchFails_thenDisplaysErrorState() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        val query = "ErrorQuery"
        scenarios.search.stubSearchError(query)

        discoverRobot.navigateToSearchTab()

        searchRobot
            .enterSearchQuery(query)
            .assertTextDisplayed("Access forbidden.", substring = true)
            .assertErrorStateDisplayed()
    }
}
