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
    fun shouldDisplaySearchResultsWhenQueryIsEntered() {
        val query = "Breaking Bad"
        val traktId = 1388L

        discoverRobot.verifyDiscoverScreenIsShown()
        discoverRobot.navigateToSearchTab()

        scenarios.search.stubSearch(query)

        searchRobot.verifySearchScreenIsShown()
        searchRobot.enterSearchQuery(query)
        searchRobot.verifySearchQuery(query)

        searchRobot.verifyResultItemIsShown(traktId)
        searchRobot.verifyResultTitleIsShown("Breaking Bad")
    }

    @Test
    fun shouldDisplayEmptyStateWhenNoResultsFound() {
        val query = "NoResultsShow"
        discoverRobot.navigateToSearchTab()

        scenarios.search.stubEmptySearch()

        searchRobot.enterSearchQuery(query)
        searchRobot.verifyEmptyStateIsShown()
        searchRobot.verifyTextShown("No results found", substring = true)
    }

    @Test
    fun shouldNavigateToShowDetailsWhenResultItemIsClicked() {
        val query = "Breaking Bad"
        val traktId = 1388L

        discoverRobot.navigateToSearchTab()

        scenarios.search.stubSearch(query)

        searchRobot.enterSearchQuery(query)
        searchRobot.clickResultItem(traktId)

        showDetailsRobot.verifyShowDetailsIsShown()
    }

    @Test
    fun shouldRestoreSearchScreenWhenNavigatingBackFromShowDetails() {
        val query = "Breaking Bad"
        val traktId = 1388L

        discoverRobot.navigateToSearchTab()

        scenarios.search.stubSearch(query)

        searchRobot.enterSearchQuery(query)
        searchRobot.clickResultItem(traktId)
        showDetailsRobot.verifyShowDetailsIsShown()

        showDetailsRobot.pressBack()

        searchRobot.verifySearchScreenIsShown()
        searchRobot.verifyResultItemIsShown(traktId)
    }

    @Test
    fun shouldDisplayErrorStateWhenSearchFails() {
        val query = "ErrorQuery"
        scenarios.search.stubSearchError(query)

        discoverRobot.navigateToSearchTab()

        searchRobot.enterSearchQuery(query)

        searchRobot.verifyTextShown("Access forbidden.", substring = true)
        searchRobot.verifyErrorStateIsShown()
    }
}
