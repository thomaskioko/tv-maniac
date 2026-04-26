package com.thomaskioko.tvmaniac.app.test.compose.flows.search

import com.thomaskioko.tvmaniac.app.test.util.BaseAppRobolectricTest
import org.junit.Before
import kotlin.test.Test

internal class SearchFlowTest : BaseAppRobolectricTest() {

    @Before
    fun setUp() {
        scenarios.stubDiscoverBrowse()
    }

    @Test
    fun `should display search results when query is entered`() {
        val query = "Breaking Bad"
        val traktId = 1388L
        val tmdbId = 1396L

        scenarios.search.stubSearch(query, traktId, tmdbId)

        homeRobot.clickDiscoverTab()
        discoverRobot.verifyDiscoverScreenIsShown()
        discoverRobot.clickSearchButton()

        searchRobot.verifySearchScreenIsShown()
        searchRobot.enterSearchQuery(query)
        searchRobot.verifySearchQuery(query)

        searchRobot.verifyResultItemIsShown(traktId)
        searchRobot.verifyResultTitleIsShown("Breaking Bad")
    }

    @Test
    fun `should display empty state when no results found`() {
        val query = "NoResultsShow"
        scenarios.search.stubEmptySearch()

        homeRobot.clickDiscoverTab()
        discoverRobot.clickSearchButton()

        searchRobot.enterSearchQuery(query)
        searchRobot.verifyEmptyStateIsShown()
        searchRobot.verifyTextShown("No results found", substring = true)
    }

    @Test
    fun `should navigate to show details when result item is clicked`() {
        val query = "Breaking Bad"
        val traktId = 1388L
        val tmdbId = 1396L

        scenarios.search.stubSearch(query, traktId, tmdbId)
        scenarios.showDetails.stubShowDetailsEndpoints(traktId)

        homeRobot.clickDiscoverTab()
        discoverRobot.clickSearchButton()

        searchRobot.enterSearchQuery(query)
        searchRobot.clickResultItem(traktId)

        showDetailsRobot.verifyShowDetailsIsShown()
    }

    @Test
    fun `should restore search screen when navigating back from show details`() {
        val query = "Breaking Bad"
        val traktId = 1388L
        val tmdbId = 1396L

        scenarios.search.stubSearch(query, traktId, tmdbId)
        scenarios.showDetails.stubShowDetailsEndpoints(traktId)

        homeRobot.clickDiscoverTab()
        discoverRobot.clickSearchButton()

        searchRobot.enterSearchQuery(query)
        searchRobot.clickResultItem(traktId)
        showDetailsRobot.verifyShowDetailsIsShown()

        showDetailsRobot.pressBack()

        searchRobot.verifySearchScreenIsShown()
        searchRobot.verifyResultItemIsShown(traktId)
    }

    @Test
    fun `should display error state when search fails`() {
        val query = "ErrorQuery"
        scenarios.search.stubSearchError(query)

        homeRobot.clickDiscoverTab()
        discoverRobot.clickSearchButton()

        searchRobot.enterSearchQuery(query)

        // Use standard robot verification with high timeout
        searchRobot.verifyTextShown("Access forbidden.", substring = true, timeoutMillis = 15_000)
        searchRobot.verifyErrorStateIsShown()
    }
}
