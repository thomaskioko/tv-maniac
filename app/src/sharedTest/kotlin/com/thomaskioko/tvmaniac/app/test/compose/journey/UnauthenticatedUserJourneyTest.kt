package com.thomaskioko.tvmaniac.app.test.compose.journey

import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import com.thomaskioko.tvmaniac.presentation.episodedetail.EpisodeSheetActionItem
import com.thomaskioko.tvmaniac.settings.presenter.ThemeModel
import com.thomaskioko.tvmaniac.testing.integration.ui.SystemDialog
import com.thomaskioko.tvmaniac.testing.integration.ui.dismissSystemDialog
import com.thomaskioko.tvmaniac.testtags.home.HomeTestTags
import org.junit.Test

internal class UnauthenticatedUserJourneyTest : BaseAppFlowTest() {

    private val breakingBadTmdbId = 1396L
    private val pilotEpisodeTraktId = 73640L
    private val secondEpisodeTraktId = 73641L
    private val betterCallSaulTmdbId = 60059L

    @Test
    fun givenUnauthenticatedUser_whenNavigatesAllScreens_thenChangesAppearanceAndNotifications() = runAppFlowTest {
        scenarios.stubUnauthenticatedJourney()

        // Verify public content on Discover
        discoverRobot
            .assertDiscoverScreenDisplayed()
            .assertShowCardDisplayed(breakingBadTmdbId)
            .assertUpNextCardDoesNotExist(breakingBadTmdbId)
            // Verify featured pager
            .assertFeaturedPagerDisplayed()
            .assertFeaturedShowDisplayed(breakingBadTmdbId)
            .swipeFeaturedPagerLeft()
            .assertFeaturedShowDisplayed(betterCallSaulTmdbId)
            .swipeFeaturedPagerRight()
            .assertFeaturedShowDisplayed(breakingBadTmdbId)

        // Search for show and navigate back
        val query = "Breaking Bad"
        scenarios.search.stubSearch(query)
        discoverRobot
            .navigateToSearchTab()
            .assertSearchScreenDisplayed()
            .enterSearchQuery(query)
            .assertResultItemDisplayed(breakingBadTmdbId)
            .clickResultItem(breakingBadTmdbId)
            .assertTrackButtonDisplayed()
            .pressBack()

        searchRobot
            .assertSearchScreenDisplayed()
            .pressBack()

        // Visit show details from Discover
        discoverRobot
            .assertFeaturedPagerDisplayed()
            .clickShowCard(breakingBadTmdbId)
            .assertDoesNotExist(HomeTestTags.NAVIGATION_BAR)
            .assertTrackButtonDisplayed()
            .pressBack()

        discoverRobot
            .assertDiscoverScreenDisplayed()

        // Verify logged-out empty states for Progress and Calendar
        homeRobot
            .clickProgressTab()
            .assertTabSelected(HomeTestTags.PROGRESS_TAB)

        progressRobot
            .assertProgressScreenDisplayed()
            .assertExists(HomeTestTags.NAVIGATION_BAR)
            .assertUpNextTabSelected()
            .assertUpNextEmptyStateDisplayed()
            .assertUpNextEpisodeDoesNotExist(breakingBadTmdbId)
            .clickCalendarTab()
            .assertCalendarTabSelected()

        calendarRobot
            .assertLoggedOutStateDisplayed()

        progressRobot
            .clickUpNextTab()

        // Verify empty watchlist state
        homeRobot.clickMyShowsTab()
            .assertTabSelected(HomeTestTags.MY_SHOWS_TAB)

        watchlistRobot
            .assertEmptyStateDisplayed()
            .assertShowCardDoesNotExist(breakingBadTmdbId)

        // Navigate to Profile and open Settings
        homeRobot.clickProfileTab()
            .assertTabSelected(HomeTestTags.PROFILE_TAB)

        profileRobot
            .assertSignInButtonDisplayed()
            .clickSettingsButton()

        settingsRobot
            .assertSettingsScreenDisplayed()
            .assertDoesNotExist(HomeTestTags.NAVIGATION_BAR)
            // Change theme from SYSTEM to DARK
            .openAppearancePage()
            .scrollToThemeSwatch(ThemeModel.SYSTEM)
            .assertThemeSwatchSelected(ThemeModel.SYSTEM)
            .scrollToThemeSwatch(ThemeModel.DARK)
            .clickThemeSwatch(ThemeModel.DARK)
            .assertThemeSwatchSelected(ThemeModel.DARK)
            .assertThemeSwatchNotSelected(ThemeModel.SYSTEM)
            .clickBackButton()
            // Dismiss episode notifications rationale
            .openNotificationsPage()
            .scrollToEpisodeNotificationsToggle()
            .assertEpisodeNotificationsDisabled()
            .clickEpisodeNotificationsToggle()

        rootRobot
            .assertNotificationRationaleDisplayed()
            .dismissNotificationRationale()

        settingsRobot
            .assertEpisodeNotificationsDisabled()
            // Accept episode notifications rationale
            .clickEpisodeNotificationsToggle()

        rootRobot
            .assertNotificationRationaleDisplayed()
            .acceptNotificationRationale()

        dismissSystemDialog(SystemDialog.NotificationPermissionDeny)

        rootRobot.assertNotificationRationaleDoesNotExist()

        // Follow show locally
        settingsRobot
            .clickBackButton()
            .clickBackButton()

        homeRobot
            .clickDiscoverTab()
            .assertTabSelected(HomeTestTags.DISCOVER_TAB)

        discoverRobot
            .assertFeaturedPagerDisplayed()
            .clickShowCard(breakingBadTmdbId)
            .assertDoesNotExist(HomeTestTags.NAVIGATION_BAR)
            .assertTrackButtonDisplayed()
            .clickTrackButton()
            .assertStopTrackingButtonDisplayed()
            .clickContinueTrackingMarkWatched(pilotEpisodeTraktId)
            .clickSeasonChip(seasonNumber = 1L)
            .assertSeasonDetailsDisplayed()
            .scrollToMarkUnwatchedButton(pilotEpisodeTraktId)
            .assertMarkUnwatchedDisplayed(pilotEpisodeTraktId)
            .clickMarkWatched(secondEpisodeTraktId)
            .scrollToMarkUnwatchedButton(secondEpisodeTraktId)
            .assertMarkUnwatchedDisplayed(secondEpisodeTraktId)
            .pressBack()

        showDetailsRobot
            .pressBack()

        discoverRobot
            .assertDiscoverScreenDisplayed()
            .assertUpNextCardDisplayed(breakingBadTmdbId)
            .clickUpNextCard(breakingBadTmdbId)
            .assertEpisodeSheetDisplayed()
            .clickActionItem(EpisodeSheetActionItem.OPEN_SHOW)

        showDetailsRobot
            .assertDoesNotExist(HomeTestTags.NAVIGATION_BAR)
            .assertStopTrackingButtonDisplayed()
            .pressBack()

        discoverRobot
            .assertDiscoverScreenDisplayed()

        // Verify offline follow in Watchlist
        homeRobot
            .clickMyShowsTab()
            .assertTabSelected(HomeTestTags.MY_SHOWS_TAB)

        watchlistRobot
            .scrollToShowCard(breakingBadTmdbId)
            .assertShowCardDisplayed(breakingBadTmdbId)
            .clickShowCard(breakingBadTmdbId)
            .assertStopTrackingButtonDisplayed()
            // Raise login-required content inside the sheet and confirm login
            .also { scenarios.stubProfileOnSignIn() }
            .clickAddToListButton()

        showListRobot
            .assertSheetDisplayed()
            .assertLoginRequiredDisplayed()
            .confirmLogin()
            .assertLoginRequiredDoesNotExist()
            .clickCloseSheetButton()
            .assertSheetDoesNotExist()

        showDetailsRobot.pressBack()

        homeRobot
            .clickProfileTab()
            .assertTabSelected(HomeTestTags.PROFILE_TAB)

        profileRobot.assertUserCardDisplayed(slug = "integration-test-user")
    }

    @Test
    fun givenUnauthenticatedUser_whenSignsInFromProfile_thenSeesUserCard() = runAppFlowTest {
        scenarios.stubUnauthenticatedJourney()
        scenarios.stubProfileOnSignIn()

        homeRobot
            .clickProfileTab()
            .assertTabSelected(HomeTestTags.PROFILE_TAB)

        profileRobot
            .assertSignInButtonDisplayed()
            .clickSignInButton()
            .assertUserCardDisplayed(slug = "integration-test-user")
    }

    @Test
    fun givenUnauthenticatedUser_whenRationaleDismissedInSettings_thenDoesNotShowAgainOnAuth() = runAppFlowTest {
        scenarios.stubUnauthenticatedJourney()

        // Dismiss rationale from Settings
        homeRobot
            .clickProfileTab()
            .assertTabSelected(HomeTestTags.PROFILE_TAB)

        profileRobot
            .clickSettingsButton()

        settingsRobot
            .assertSettingsScreenDisplayed()
            .openNotificationsPage()
            .scrollToEpisodeNotificationsToggle()
            .clickEpisodeNotificationsToggle()

        rootRobot
            .assertNotificationRationaleDisplayed()
            .dismissNotificationRationale()
            .assertNotificationRationaleDoesNotExist()

        settingsRobot
            .clickBackButton()
            .clickBackButton()

        // Login user
        profileRobot
            .assertSignInButtonDisplayed()

        scenarios.stubProfileOnSignIn()

        profileRobot
            .clickSignInButton()
            .assertUserCardDisplayed(slug = "integration-test-user")

        // Verify rationale remains hidden after auth transition
        profileRobot
            .clickSettingsButton()

        settingsRobot
            .assertSettingsScreenDisplayed()
            .clickBackButton()

        profileRobot
            .assertUserCardDisplayed(slug = "integration-test-user")

        rootRobot
            .assertNotificationRationaleDoesNotExist()
    }
}
