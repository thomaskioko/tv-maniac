package com.thomaskioko.tvmaniac.app.test.compose.journey

import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import com.thomaskioko.tvmaniac.presentation.episodedetail.EpisodeSheetActionItem
import com.thomaskioko.tvmaniac.settings.presenter.ThemeModel
import com.thomaskioko.tvmaniac.testing.integration.ui.SystemDialog
import com.thomaskioko.tvmaniac.testing.integration.ui.dismissSystemDialog
import org.junit.Before
import org.junit.Test

internal class UnauthenticatedUserJourneyTest : BaseAppFlowTest() {

    private val breakingBadTraktId = 1388L
    private val breakingBadTmdbId = 1396L
    private val breakingBadSeasons = listOf(1L, 2L)
    private val pilotEpisodeTraktId = 73640L
    private val secondEpisodeTraktId = 73641L
    private val betterCallSaulTraktId = 59660L

    @Before
    fun stubEndpoints() {
        scenarios.stubUnauthenticatedJourney()
    }

    @Test
    fun unauthenticatedUserNavigatesAllScreensAndChangesAppearanceAndNotifications() {
        // Verify public content on Discover
        discoverRobot.verifyDiscoverScreenIsShown()
        discoverRobot.verifyShowCardIsShown(breakingBadTraktId)
        discoverRobot.verifyUpNextCardIsHidden(breakingBadTraktId)

        // Verify featured pager
        discoverRobot.verifyFeaturedPagerIsShown()
        discoverRobot.verifyFeaturedShowIsShown(breakingBadTraktId)
        discoverRobot.swipeFeaturedPagerLeft()
        discoverRobot.verifyFeaturedShowIsShown(betterCallSaulTraktId)
        discoverRobot.swipeFeaturedPagerRight()
        discoverRobot.verifyFeaturedShowIsShown(breakingBadTraktId)

        // Search for show and navigate back
        val query = "Breaking Bad"
        scenarios.search.stubSearch(query)
        discoverRobot.navigateToSearchTab()
        searchRobot.verifySearchScreenIsShown()
        searchRobot.enterSearchQuery(query)
        searchRobot.verifyResultItemIsShown(breakingBadTraktId)
        searchRobot.clickResultItem(breakingBadTraktId)
        showDetailsRobot.verifyTrackButtonIsShown()
        showDetailsRobot.pressBack()
        searchRobot.verifySearchScreenIsShown()
        searchRobot.pressBack()

        // Visit show details from Discover
        discoverRobot.clickShowCard(breakingBadTraktId)
        showDetailsRobot.verifyTrackButtonIsShown()
        showDetailsRobot.pressBack()
        discoverRobot.verifyDiscoverScreenIsShown()

        // Verify logged-out empty states for Progress and Calendar
        homeRobot.clickProgressTab()
        progressRobot.verifyProgressScreenIsShown()
        progressRobot.verifyUpNextEmptyStateIsShown()
        progressRobot.verifyEpisodeRowIsHidden(breakingBadTraktId)

        progressRobot.clickCalendarTab()
        calendarRobot.verifyLoggedOutStateIsShown()
        progressRobot.clickUpNextTab()

        // Verify empty library state
        homeRobot.clickLibraryTab()
        libraryRobot.verifyEmptyStateIsShown()
        libraryRobot.verifyShowRowIsHidden(breakingBadTraktId)

        // Navigate to Profile and open Settings
        homeRobot.clickProfileTab()
        profileRobot.verifySignInButtonIsShown()
        profileRobot.clickSettingsButton()
        settingsRobot.verifySettingsScreenIsShown()

        // Change theme from SYSTEM to DARK
        settingsRobot.scrollToThemeSwatch(ThemeModel.SYSTEM)
        settingsRobot.verifyThemeSwatchSelected(ThemeModel.SYSTEM)
        settingsRobot.scrollToThemeSwatch(ThemeModel.DARK)
        settingsRobot.clickThemeSwatch(ThemeModel.DARK)
        settingsRobot.verifyThemeSwatchSelected(ThemeModel.DARK)
        settingsRobot.verifyThemeSwatchNotSelected(ThemeModel.SYSTEM)

        // Dismiss episode notifications rationale
        settingsRobot.scrollToEpisodeNotificationsToggle()
        settingsRobot.verifyEpisodeNotificationsDisabled()
        settingsRobot.clickEpisodeNotificationsToggle()
        rootRobot.verifyNotificationRationaleIsShown()
        rootRobot.verifyNotificationRationaleIsShownAndDismissed()
        settingsRobot.verifyEpisodeNotificationsDisabled()

        // Accept episode notifications rationale
        settingsRobot.clickEpisodeNotificationsToggle()
        rootRobot.verifyNotificationRationaleIsShown()
        rootRobot.acceptNotificationRationale()
        rootRobot.verifyNotificationRationaleIsHidden()
        composeTestRule.dismissSystemDialog(SystemDialog.NotificationPermissionDeny)

        // Follow show locally
        settingsRobot.pressBack()
        homeRobot.clickDiscoverTab()
        discoverRobot.clickShowCard(breakingBadTraktId)
        showDetailsRobot.verifyTrackButtonIsShown()
        showDetailsRobot.clickTrackButton()
        showDetailsRobot.verifyStopTrackingButtonIsShown()

        // Continue tracking and mark season watched
        showDetailsRobot.clickContinueTrackingMarkWatched(pilotEpisodeTraktId)

        showDetailsRobot.clickSeasonChip(seasonNumber = 1L)
        seasonDetailsRobot.verifySeasonDetailsIsShown()
        seasonDetailsRobot.verifyMarkUnwatchedIsShown(pilotEpisodeTraktId)
        seasonDetailsRobot.clickMarkWatched(secondEpisodeTraktId)
        seasonDetailsRobot.verifyMarkUnwatchedIsShown(secondEpisodeTraktId)

        seasonDetailsRobot.pressBack()
        showDetailsRobot.verifyStopTrackingButtonIsShown()
        showDetailsRobot.pressBack()
        discoverRobot.verifyDiscoverScreenIsShown()

        // Open Episode Sheet from Discover UpNext card
        discoverRobot.verifyUpNextCardIsShown(breakingBadTraktId)
        discoverRobot.clickUpNextCard(breakingBadTraktId)
        episodeSheetRobot.verifyEpisodeSheetIsShown()
        episodeSheetRobot.clickActionItem(EpisodeSheetActionItem.OPEN_SHOW)
        showDetailsRobot.verifyStopTrackingButtonIsShown()
        showDetailsRobot.pressBack()
        discoverRobot.verifyDiscoverScreenIsShown()

        // Verify offline follow in Library
        homeRobot.clickLibraryTab()
        libraryRobot.verifyShowRowIsShown(breakingBadTraktId)
        libraryRobot.clickShowRow(breakingBadTraktId)
        showDetailsRobot.verifyStopTrackingButtonIsShown()

        // Raise login prompt and confirm login
        scenarios.stubProfileOnSignIn()
        showDetailsRobot.clickAddToListButton()
        showDetailsRobot.verifyLoginPromptIsShown()
        showDetailsRobot.confirmLoginPrompt()
        showDetailsRobot.verifyLoginPromptIsHidden()
        showDetailsRobot.pressBack()
        homeRobot.clickProfileTab()
        profileRobot.verifyUserCardIsShown(slug = "integration-test-user")
    }

    @Test
    fun unauthenticatedUserSignsInFromProfileAndSeesUserCard() {
        scenarios.stubProfileOnSignIn()
        homeRobot.clickProfileTab()
        profileRobot.verifySignInButtonIsShown()
        profileRobot.clickSignInButton()
        profileRobot.verifyUserCardIsShown(slug = "integration-test-user")
    }

    @Test
    fun unauthenticatedUserDoesNotSeeAuthRationaleAfterPriorSettingsDismiss() {
        // Dismiss rationale from Settings
        homeRobot.clickProfileTab()

        profileRobot.clickSettingsButton()
        settingsRobot.verifySettingsScreenIsShown()

        settingsRobot.scrollToEpisodeNotificationsToggle()
        settingsRobot.clickEpisodeNotificationsToggle()
        rootRobot.verifyNotificationRationaleIsShown()
        rootRobot.verifyNotificationRationaleIsShownAndDismissed()
        rootRobot.verifyNotificationRationaleIsHidden()

        // Login user
        settingsRobot.pressBack()
        profileRobot.verifySignInButtonIsShown()

        scenarios.stubProfileOnSignIn()

        profileRobot.clickSignInButton()
        profileRobot.verifyUserCardIsShown(slug = "integration-test-user")

        // Verify rationale remains hidden after auth transition
        profileRobot.clickSettingsButton()
        settingsRobot.verifySettingsScreenIsShown()
        settingsRobot.pressBack()
        profileRobot.verifyUserCardIsShown(slug = "integration-test-user")

        rootRobot.verifyNotificationRationaleIsHidden()
    }
}
