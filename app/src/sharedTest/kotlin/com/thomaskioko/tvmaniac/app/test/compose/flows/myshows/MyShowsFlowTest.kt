package com.thomaskioko.tvmaniac.app.test.compose.flows.myshows

import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import com.thomaskioko.tvmaniac.testtags.home.HomeTestTags
import org.junit.Test

internal class MyShowsFlowTest : BaseAppFlowTest() {

    private val breakingBadTraktId = 1388L
    private val forAllMankindTraktId = 140481L
    private val theBoysTraktId = 139960L

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
            .scrollToShowCard(breakingBadTraktId)
            .assertShowCardDisplayed(breakingBadTraktId)
            .scrollToShowCard(forAllMankindTraktId)
            .assertShowCardDisplayed(forAllMankindTraktId)
            .scrollToShowCard(theBoysTraktId)
            .assertShowCardDisplayed(theBoysTraktId)

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
            .clickShowCard(breakingBadTraktId)
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
            .assertShowCardDoesNotExist(breakingBadTraktId)
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
            .assertShowCardDisplayed(breakingBadTraktId)
            .assertShowCardDisplayed(forAllMankindTraktId)
            .assertShowCardDisplayed(theBoysTraktId)
            .clickSearchButton()
            .enterSearchQuery("Breaking")
            .assertShowCardDisplayed(breakingBadTraktId)
            .assertShowCardDoesNotExist(forAllMankindTraktId)
            .assertShowCardDoesNotExist(theBoysTraktId)
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
            .assertShowCardDisplayed(breakingBadTraktId)
            .clickSearchButton()
            .enterSearchQuery("breaking")
            .assertShowCardDisplayed(breakingBadTraktId)
            .assertShowCardDoesNotExist(forAllMankindTraktId)
            .assertShowCardDoesNotExist(theBoysTraktId)
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
            .assertShowCardDisplayed(breakingBadTraktId)
            .clickSearchButton()
            .enterSearchQuery("Zzzzz")
            .assertEmptyStateDisplayed()
            .assertShowCardDoesNotExist(breakingBadTraktId)
            .assertShowCardDoesNotExist(forAllMankindTraktId)
            .assertShowCardDoesNotExist(theBoysTraktId)
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
            .assertShowCardDisplayed(breakingBadTraktId)
            .assertShowCardDoesNotExist(forAllMankindTraktId)
            .enterSearchQuery("")
            .assertShowCardDisplayed(breakingBadTraktId)
            .assertShowCardDisplayed(forAllMankindTraktId)
            .assertShowCardDisplayed(theBoysTraktId)
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
