package com.thomaskioko.tvmaniac.app.test.compose.flows.search

import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import org.junit.Before
import org.junit.Test

internal class SearchFlowTest : BaseAppFlowTest() {

    @Before
    fun setUp() {
        scenarios.discover.stubBrowseGraph()
    }

    @Test
    fun givenSearch_whenQueryEntered_thenDisplaysResults() {
        val query = "Breaking Bad"
        val traktId = 1388L

        discoverRobot.assertDiscoverScreenDisplayed()
        discoverRobot.navigateToSearchTab()

        scenarios.search.stubSearch(query)

        searchRobot.assertSearchScreenDisplayed()
        searchRobot.enterSearchQuery(query)
        searchRobot.assertSearchQueryDisplayed(query)

        searchRobot.assertResultItemDisplayed(traktId)
        searchRobot.assertResultTitleDisplayed("Breaking Bad")
    }

    @Test
    fun givenSearch_whenNoResultsFound_thenDisplaysEmptyState() {
        val query = "NoResultsShow"
        discoverRobot.navigateToSearchTab()

        scenarios.search.stubEmptySearch()

        searchRobot.enterSearchQuery(query)
        searchRobot.assertEmptyStateDisplayed()
        searchRobot.assertTextDisplayed("No results found", substring = true)
    }

    @Test
    fun givenSearch_whenResultItemClicked_thenNavigatesToShowDetails() {
        val query = "Breaking Bad"
        val traktId = 1388L

        discoverRobot.navigateToSearchTab()

        scenarios.search.stubSearch(query)

        searchRobot.enterSearchQuery(query)
        searchRobot.clickResultItem(traktId)

        showDetailsRobot.assertShowDetailsDisplayed()
    }

    @Test
    fun givenShowDetails_whenBackIsPressed_thenRestoresSearchScreen() {
        val query = "Breaking Bad"
        val traktId = 1388L

        discoverRobot.navigateToSearchTab()

        scenarios.search.stubSearch(query)

        searchRobot.enterSearchQuery(query)
        searchRobot.clickResultItem(traktId)
        showDetailsRobot.assertShowDetailsDisplayed()

        showDetailsRobot.pressBack()

        searchRobot.assertSearchScreenDisplayed()
        searchRobot.assertResultItemDisplayed(traktId)
    }

    @Test
    fun givenSearch_whenSearchFails_thenDisplaysErrorState() {
        val query = "ErrorQuery"
        scenarios.search.stubSearchError(query)

        discoverRobot.navigateToSearchTab()

        searchRobot.enterSearchQuery(query)

        searchRobot.assertTextDisplayed("Access forbidden.", substring = true)
        searchRobot.assertErrorStateDisplayed()
    }
}
