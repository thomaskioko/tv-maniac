package com.thomaskioko.tvmaniac.app.test.compose.journey

import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import com.thomaskioko.tvmaniac.app.test.compose.stubs.TEST_PROFILE_SLUG
import com.thomaskioko.tvmaniac.app.test.compose.stubs.TEST_SIMKL_ACCOUNT_ID
import com.thomaskioko.tvmaniac.testtags.home.HomeTestTags
import org.junit.Test

internal class AccountProviderSwitchJourneyTest : BaseAppFlowTest() {

    private val breakingBadTmdbId = 1396L

    @Test
    fun givenTraktSession_whenSwitchesToSimkl_thenActiveBecomesSimklAndDiscoverStaysIntact() = runAppFlowTest {
        scenarios.flags.enableSimklLogin()
        scenarios.flags.enableAccountSwitch()
        scenarios.stubAuthenticatedSync()

        // Trakt-synced My Shows is populated before the switch.
        homeRobot
            .clickMyShowsTab()
            .assertTabSelected(HomeTestTags.MY_SHOWS_TAB)
        watchlistRobot
            .scrollToShowCard(breakingBadTmdbId)
            .assertShowCardDisplayed(breakingBadTmdbId)

        // Tapping "Switch to Simkl" launches Simkl sign-in; the OAuth callback seeds the Simkl session.
        graph.oAuthLauncher.setOnLaunch {
            scenarios.stubAuthenticatedSimklProfile()
        }

        homeRobot
            .clickProfileTab()
            .assertTabSelected(HomeTestTags.PROFILE_TAB)
        profileRobot
            .assertUserCardDisplayed(slug = TEST_PROFILE_SLUG)
            .clickSettingsButton()

        settingsRobot
            .assertSettingsScreenDisplayed()
            .openTraktPage()
            .scrollToSwitchProviderButton()
            .assertSwitchProviderButtonDisplayed()
            .clickSwitchProviderButton()
            .clickBackButton()
            .clickBackButton()

        // The active account is now Simkl: Profile renders the Simkl user.
        profileRobot
            .assertUserCardDisplayed(slug = TEST_SIMKL_ACCOUNT_ID.toString())
            .assertUserNameDisplayed()

        // Discover is provider-agnostic and remains intact after the switch.
        homeRobot
            .clickDiscoverTab()
            .assertTabSelected(HomeTestTags.DISCOVER_TAB)
        discoverRobot
            .assertFeaturedShowDisplayed(breakingBadTmdbId)
    }
}
