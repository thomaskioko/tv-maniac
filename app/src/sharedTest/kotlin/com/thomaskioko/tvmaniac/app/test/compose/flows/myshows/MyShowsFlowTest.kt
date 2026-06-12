package com.thomaskioko.tvmaniac.app.test.compose.flows.myshows

import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import com.thomaskioko.tvmaniac.testtags.home.HomeTestTags
import org.junit.Test

internal class MyShowsFlowTest : BaseAppFlowTest() {

    private val breakingBadTmdbId = 1396L
    private val forAllMankindTmdbId = 87917L
    private val theBoysTmdbId = 76479L

    @Test
    fun givenAuthenticatedUser_whenWatchlistOpened_thenShowsAllWatchedShows() = runAppFlowTest {
        scenarios.stubAuthenticatedSync()

        rootRobot
            .dismissNotificationRationale()

        discoverRobot
            .assertDiscoverScreenDisplayed()

        homeRobot
            .clickMyShowsTab()
            .assertTabSelected(HomeTestTags.MY_SHOWS_TAB)

        watchlistRobot
            .assertMyShowsScreenDisplayed()
            .scrollToShowCard(breakingBadTmdbId)
            .assertShowCardDisplayed(breakingBadTmdbId)
            .scrollToShowCard(forAllMankindTmdbId)
            .assertShowCardDisplayed(forAllMankindTmdbId)
            .scrollToShowCard(theBoysTmdbId)
            .assertShowCardDisplayed(theBoysTmdbId)

        rootRobot
            .assertNoSnackbarDisplayed()
    }

    @Test
    fun givenWatchlistShowCard_whenClicked_thenNavigatesToShowDetails() = runAppFlowTest {
        scenarios.stubAuthenticatedSync()

        rootRobot
            .dismissNotificationRationale()

        discoverRobot
            .assertDiscoverScreenDisplayed()

        homeRobot
            .clickMyShowsTab()
            .assertTabSelected(HomeTestTags.MY_SHOWS_TAB)

        watchlistRobot
            .clickShowCard(breakingBadTmdbId)
            .assertShowDetailsDisplayed()
    }

    @Test
    fun givenUnauthenticatedUser_whenWatchlistOpened_thenShowsEmptyState() = runAppFlowTest {
        scenarios.stubUnauthenticatedJourney()

        discoverRobot
            .assertDiscoverScreenDisplayed()

        homeRobot
            .clickMyShowsTab()
            .assertTabSelected(HomeTestTags.MY_SHOWS_TAB)

        watchlistRobot
            .assertMyShowsScreenDisplayed()
            .assertEmptyStateDisplayed()
            .assertShowCardDoesNotExist(breakingBadTmdbId)
    }

    @Test
    fun givenWatchlist_whenSearchQueryEntered_thenFiltersResults() = runAppFlowTest {
        scenarios.stubAuthenticatedSync()

        rootRobot
            .dismissNotificationRationale()

        discoverRobot
            .assertDiscoverScreenDisplayed()

        homeRobot
            .click(HomeTestTags.MY_SHOWS_TAB)
            .assertTabSelected(HomeTestTags.MY_SHOWS_TAB)

        watchlistRobot
            .assertShowCardDisplayed(breakingBadTmdbId)
            .assertShowCardDisplayed(forAllMankindTmdbId)
            .assertShowCardDisplayed(theBoysTmdbId)
            .clickSearchButton()
            .enterSearchQuery("Breaking")
            .assertShowCardDisplayed(breakingBadTmdbId)
            .assertShowCardDoesNotExist(forAllMankindTmdbId)
            .assertShowCardDoesNotExist(theBoysTmdbId)
    }

    @Test
    fun givenWatchlist_whenLowercaseQueryEntered_thenFiltersCaseInsensitive() = runAppFlowTest {
        scenarios.stubAuthenticatedSync()

        rootRobot.dismissNotificationRationale()
        discoverRobot.assertDiscoverScreenDisplayed()

        homeRobot
            .clickMyShowsTab()
            .assertTabSelected(HomeTestTags.MY_SHOWS_TAB)

        watchlistRobot
            .assertShowCardDisplayed(breakingBadTmdbId)
            .clickSearchButton()
            .enterSearchQuery("breaking")
            .assertShowCardDisplayed(breakingBadTmdbId)
            .assertShowCardDoesNotExist(forAllMankindTmdbId)
            .assertShowCardDoesNotExist(theBoysTmdbId)
    }

    @Test
    fun givenWatchlist_whenSearchQueryYieldsNoMatches_thenShowsEmptyState() = runAppFlowTest {
        scenarios.stubAuthenticatedSync()

        rootRobot.dismissNotificationRationale()
        discoverRobot.assertDiscoverScreenDisplayed()

        homeRobot
            .clickMyShowsTab()
            .assertTabSelected(HomeTestTags.MY_SHOWS_TAB)

        watchlistRobot
            .assertShowCardDisplayed(breakingBadTmdbId)
            .clickSearchButton()
            .enterSearchQuery("Zzzzz")
            .assertEmptyStateDisplayed()
            .assertShowCardDoesNotExist(breakingBadTmdbId)
            .assertShowCardDoesNotExist(forAllMankindTmdbId)
            .assertShowCardDoesNotExist(theBoysTmdbId)
    }

    @Test
    fun givenWatchlistFiltered_whenSearchQueryCleared_thenAllCardsReturn() = runAppFlowTest {
        scenarios.stubAuthenticatedSync()

        rootRobot.dismissNotificationRationale()
        discoverRobot.assertDiscoverScreenDisplayed()

        homeRobot
            .clickMyShowsTab()
            .assertTabSelected(HomeTestTags.MY_SHOWS_TAB)

        watchlistRobot
            .clickSearchButton()
            .enterSearchQuery("Breaking")
            .assertShowCardDisplayed(breakingBadTmdbId)
            .assertShowCardDoesNotExist(forAllMankindTmdbId)
            .enterSearchQuery("")
            .assertShowCardDisplayed(breakingBadTmdbId)
            .assertShowCardDisplayed(forAllMankindTmdbId)
            .assertShowCardDisplayed(theBoysTmdbId)
    }

    @Test
    fun givenWatchlistGrid_whenToggleListStyleClicked_thenSwitchesToListMode() = runAppFlowTest {
        scenarios.stubAuthenticatedSync()

        rootRobot.dismissNotificationRationale()
        discoverRobot.assertDiscoverScreenDisplayed()

        homeRobot
            .clickMyShowsTab()
            .assertTabSelected(HomeTestTags.MY_SHOWS_TAB)

        watchlistRobot
            .assertGridDisplayed()
            .clickToggleListStyleButton()
            .assertListDisplayed()
            .clickToggleListStyleButton()
            .assertGridDisplayed()
    }

    @Test
    fun givenWatchlist_whenSortButtonTapped_thenSortSheetOpens() = runAppFlowTest {
        scenarios.stubAuthenticatedSync()

        rootRobot.dismissNotificationRationale()
        discoverRobot.assertDiscoverScreenDisplayed()

        homeRobot
            .clickMyShowsTab()
            .assertTabSelected(HomeTestTags.MY_SHOWS_TAB)

        watchlistRobot
            .assertGridDisplayed()
            .clickSortButton()
            .assertSortSheetDisplayed()
    }
}
