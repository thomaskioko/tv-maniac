package com.thomaskioko.tvmaniac.app.test.compose.flows.search

import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import com.thomaskioko.tvmaniac.testtags.home.HomeTestTags
import org.junit.Test

internal class SearchFlowTest : BaseAppFlowTest() {

    @Test
    fun searchUserJourney() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        val query = "Breaking Bad"
        val tmdbId = 1396L

        discoverRobot
            .assertDiscoverScreenDisplayed()
            .navigateToSearchTab()

        scenarios.search.stubSearch(query)

        // 1. Enter Query & Verify results
        searchRobot
            .assertSearchScreenDisplayed()
            .assertDoesNotExist(HomeTestTags.NAVIGATION_BAR)
            .enterSearchQuery(query)
            .assertSearchQueryDisplayed(query)
            .assertResultItemDisplayed(tmdbId)
            .assertResultCountEquals(1)
            .assertResultTitleDisplayed("Breaking Bad")
            // 2. Click Result -> Show Details
            .clickResultItem(tmdbId)
            .assertShowDetailsDisplayed()
            .pressBack()

        // 3. Back -> Search Screen restored
        searchRobot
            .assertSearchScreenDisplayed()
            .assertResultItemDisplayed(tmdbId)
    }

    @Test
    fun searchRecentSearchesJourney() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        val query = "Breaking Bad"
        val tmdbId = 1396L

        discoverRobot
            .assertDiscoverScreenDisplayed()
            .navigateToSearchTab()

        scenarios.search.stubSearch(query)

        searchRobot
            .assertSearchScreenDisplayed()
            .enterSearchQuery(query)
            .assertResultItemDisplayed(tmdbId)
            .clickResultItem(tmdbId)
            .assertShowDetailsDisplayed()
            .pressBack()

        searchRobot
            .assertSearchScreenDisplayed()
            .clearSearchQuery()
            .assertRecentSearchChipDisplayed(query)
            .clickRecentSearchChip(query)
            .assertResultItemDisplayed(tmdbId)

        searchRobot
            .clearSearchQuery()
            .assertRecentSearchesSectionDisplayed()
            .clickClearRecentSearches()
            .assertRecentSearchesSectionNotDisplayed()
    }

    @Test
    fun givenSearchResults_whenSearchKeyPressed_thenFetchesAgain() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        val query = "Breaking Bad"
        val tmdbId = 1396L
        scenarios.search.stubSearch(query)

        discoverRobot
            .assertDiscoverScreenDisplayed()
            .navigateToSearchTab()

        searchRobot
            .assertSearchScreenDisplayed()
            .enterSearchQuery(query)
            .assertResultItemDisplayed(tmdbId)

        scenarios.search.stubSearchError(query)

        searchRobot
            .pressImeSearchAction()
            .assertTextDisplayed("Access forbidden.", substring = true)
            .assertResultItemDisplayed(tmdbId)
    }

    @Test
    fun givenSearch_whenSearchFails_thenDisplaysErrorState() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        val query = "ErrorQuery"
        scenarios.search.stubSearchError(query)

        discoverRobot
            .navigateToSearchTab()

        searchRobot
            .enterSearchQuery(query)
            .assertTextDisplayed("Access forbidden.", substring = true)
            .assertErrorStateDisplayed()
    }
}
