package com.thomaskioko.tvmaniac.app.test.compose.flows.search

import com.thomaskioko.tvmaniac.app.test.util.BaseAppFlowTest
import org.junit.Before
import org.junit.Test

internal class SearchFlowTest : BaseAppFlowTest() {

    @Before
    fun setUp() {
        scenarios.stubDiscoverBrowse()
    }

    @Test
    fun shouldDisplaySearchResultsWhenQueryIsEntered() {
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
    fun shouldDisplayEmptyStateWhenNoResultsFound() {
        val query = "NoResultsShow"
        scenarios.search.stubEmptySearch()

        homeRobot.clickDiscoverTab()
        discoverRobot.clickSearchButton()

        searchRobot.enterSearchQuery(query)
        searchRobot.verifyEmptyStateIsShown()
        searchRobot.verifyTextShown("No results found", substring = true)
    }

    @Test
    fun shouldNavigateToShowDetailsWhenResultItemIsClicked() {
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
    fun shouldRestoreSearchScreenWhenNavigatingBackFromShowDetails() {
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
    fun shouldDisplayErrorStateWhenSearchFails() {
        val query = "ErrorQuery"
        scenarios.search.stubSearchError(query)

        homeRobot.clickDiscoverTab()
        discoverRobot.clickSearchButton()

        searchRobot.enterSearchQuery(query)

        searchRobot.verifyTextShown("Access forbidden.", substring = true, timeoutMillis = 15_000)
        searchRobot.verifyErrorStateIsShown()
    }
}
