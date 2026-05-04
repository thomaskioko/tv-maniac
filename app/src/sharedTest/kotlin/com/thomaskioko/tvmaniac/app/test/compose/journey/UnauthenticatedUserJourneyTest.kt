package com.thomaskioko.tvmaniac.app.test.compose.journey

import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import com.thomaskioko.tvmaniac.presentation.episodedetail.EpisodeSheetActionItem
import com.thomaskioko.tvmaniac.settings.presenter.ThemeModel
import com.thomaskioko.tvmaniac.testing.integration.ui.SystemDialog
import com.thomaskioko.tvmaniac.testing.integration.ui.dismissSystemDialog
import com.thomaskioko.tvmaniac.testtags.home.HomeTestTags
import org.junit.Test

internal class UnauthenticatedUserJourneyTest : BaseAppFlowTest() {

    private val breakingBadTraktId = 1388L
    private val pilotEpisodeTraktId = 73640L
    private val secondEpisodeTraktId = 73641L
    private val betterCallSaulTraktId = 59660L

    @Test
    fun givenUnauthenticatedUser_whenNavigatesAllScreens_thenChangesAppearanceAndNotifications() = runAppFlowTest {
        scenarios.stubUnauthenticatedJourney()

        // Verify public content on Discover
        discoverRobot
            .assertLoadingIndicatorDisplayed()
            .assertDiscoverScreenDisplayed()
            .assertShowCardDisplayed(breakingBadTraktId)
            .assertUpNextCardDoesNotExist(breakingBadTraktId)
            // Verify featured pager
            .assertFeaturedPagerDisplayed()
            .assertFeaturedShowDisplayed(breakingBadTraktId)
            .swipeFeaturedPagerLeft()
            .assertFeaturedShowDisplayed(betterCallSaulTraktId)
            .swipeFeaturedPagerRight()
            .assertFeaturedShowDisplayed(breakingBadTraktId)

        // Search for show and navigate back
        val query = "Breaking Bad"
        scenarios.search.stubSearch(query)
        discoverRobot
            .navigateToSearchTab()
            .assertSearchScreenDisplayed()
            .enterSearchQuery(query)
            .assertResultItemDisplayed(breakingBadTraktId)
            .clickResultItem(breakingBadTraktId)
            .assertTrackButtonDisplayed()
            .pressBack()

        searchRobot
            .assertSearchScreenDisplayed()
            .pressBack()

        // Visit show details from Discover
        discoverRobot
            .assertFeaturedPagerDisplayed()
            .clickShowCard(breakingBadTraktId)
            .assertTrackButtonDisplayed()
            .pressBack()

        discoverRobot.assertDiscoverScreenDisplayed()

        // Verify logged-out empty states for Progress and Calendar
        homeRobot.clickProgressTab()
            .assertTabSelected(HomeTestTags.PROGRESS_TAB)

        progressRobot
            .assertProgressScreenDisplayed()
            .assertUpNextTabSelected()
            .assertUpNextEmptyStateDisplayed()
            .assertUpNextEpisodeDoesNotExist(breakingBadTraktId)
            .clickCalendarTab()
            .assertCalendarTabSelected()

        calendarRobot.assertLoggedOutStateDisplayed()

        progressRobot.clickUpNextTab()

        // Verify empty library state
        homeRobot.clickLibraryTab()
            .assertTabSelected(HomeTestTags.LIBRARY_TAB)

        libraryRobot
            .assertEmptyStateDisplayed()
            .assertShowRowDoesNotExist(breakingBadTraktId)

        // Navigate to Profile and open Settings
        homeRobot.clickProfileTab()
            .assertTabSelected(HomeTestTags.PROFILE_TAB)

        profileRobot
            .scrollToSignInButton()
            .assertSignInButtonDisplayed()
            .clickSettingsButton()

        settingsRobot
            .assertSettingsScreenDisplayed()
            // Change theme from SYSTEM to DARK
            .scrollToThemeSwatch(ThemeModel.SYSTEM)
            .assertThemeSwatchSelected(ThemeModel.SYSTEM)
            .scrollToThemeSwatch(ThemeModel.DARK)
            .clickThemeSwatch(ThemeModel.DARK)
            .assertThemeSwatchSelected(ThemeModel.DARK)
            .assertThemeSwatchNotSelected(ThemeModel.SYSTEM)
            // Dismiss episode notifications rationale
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
        settingsRobot.clickBackButton()

        homeRobot.clickDiscoverTab()
            .assertTabSelected(HomeTestTags.DISCOVER_TAB)

        discoverRobot
            .assertFeaturedPagerDisplayed()
            .clickShowCard(breakingBadTraktId)
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

        showDetailsRobot.pressBack()

        discoverRobot
            .assertDiscoverScreenDisplayed()
            .assertUpNextCardDisplayed(breakingBadTraktId)
            .clickUpNextCard(breakingBadTraktId)
            .assertEpisodeSheetDisplayed()
            .clickActionItem(EpisodeSheetActionItem.OPEN_SHOW)

        showDetailsRobot
            .assertStopTrackingButtonDisplayed()
            .pressBack()

        discoverRobot.assertDiscoverScreenDisplayed()

        // Verify offline follow in Library
        homeRobot
            .clickLibraryTab()
            .assertTabSelected(HomeTestTags.LIBRARY_TAB)

        libraryRobot
            .scrollToShowRow(breakingBadTraktId)
            .assertShowRowDisplayed(breakingBadTraktId)
            .clickShowRow(breakingBadTraktId)
            .assertStopTrackingButtonDisplayed()
            // Raise login prompt and confirm login
            .also { scenarios.stubProfileOnSignIn() }
            .clickAddToListButton()
            .assertLoginPromptDisplayed()
            .confirmLoginPrompt()
            .assertLoginPromptDoesNotExist()
            .pressBack()

        homeRobot.clickProfileTab()
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
            .scrollToSignInButton()
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
            .scrollToEpisodeNotificationsToggle()
            .clickEpisodeNotificationsToggle()

        rootRobot
            .assertNotificationRationaleDisplayed()
            .dismissNotificationRationale()
            .assertNotificationRationaleDoesNotExist()

        settingsRobot
            .clickBackButton()

        // Login user
        profileRobot
            .scrollToSignInButton()
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
