package com.thomaskioko.tvmaniac.app.test.compose.flows.search

import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import org.junit.Test

internal class SearchFlowTest : BaseAppFlowTest() {

    @Test
    fun searchUserJourney() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        val query = "Breaking Bad"
        val traktId = 1388L

        discoverRobot.assertDiscoverScreenDisplayed()
        discoverRobot.navigateToSearchTab()

        scenarios.search.stubSearch(query)

        // 1. Enter Query & Verify results
        searchRobot.assertSearchScreenDisplayed()
        searchRobot.enterSearchQuery(query)
        searchRobot.assertSearchQueryDisplayed(query)
        searchRobot.assertResultItemDisplayed(traktId)
        searchRobot.assertResultTitleDisplayed("Breaking Bad")

        // 2. Click Result -> Show Details
        searchRobot.clickResultItem(traktId)
        showDetailsRobot.assertShowDetailsDisplayed()

        // 3. Back -> Search Screen restored
        rootRobot.pressBack()
        searchRobot.assertSearchScreenDisplayed()
        searchRobot.assertResultItemDisplayed(traktId)
    }

    @Test
    fun givenSearch_whenSearchFails_thenDisplaysErrorState() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        val query = "ErrorQuery"
        scenarios.search.stubSearchError(query)

        discoverRobot.navigateToSearchTab()

        searchRobot.enterSearchQuery(query)

        searchRobot.assertTextDisplayed("Access forbidden.", substring = true)
        searchRobot.assertErrorStateDisplayed()
    }
}
