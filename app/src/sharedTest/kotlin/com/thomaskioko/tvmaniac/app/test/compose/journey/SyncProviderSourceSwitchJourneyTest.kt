package com.thomaskioko.tvmaniac.app.test.compose.journey

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import com.thomaskioko.tvmaniac.app.test.compose.stubs.TEST_PROFILE_SLUG
import com.thomaskioko.tvmaniac.app.test.compose.stubs.TEST_SIMKL_ACCOUNT_ID
import com.thomaskioko.tvmaniac.testtags.home.HomeTestTags
import com.thomaskioko.tvmaniac.util.testing.FlakyTests
import org.junit.Test

internal class SyncProviderSourceSwitchJourneyTest : BaseAppFlowTest() {

    private val breakingBadTmdbId = 1396L

    @FlakyTests(count = 3)
    @Test
    fun givenTraktSession_whenSwitchesToSimkl_thenActiveBecomesSimklAndDiscoverStaysIntact() = runAppFlowTest {
        scenarios.stubAuthenticatedSyncWithAccountSwitch()

        homeRobot
            .clickMyShowsTab()
            .assertTabSelected(HomeTestTags.MY_SHOWS_TAB)
        watchlistRobot
            .scrollToShowCard(breakingBadTmdbId)
            .assertShowCardDisplayed(breakingBadTmdbId)

        scenarios.stubOnSignIn(SyncProviderSource.SIMKL)

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

        profileRobot
            .assertUserCardDisplayed(slug = TEST_SIMKL_ACCOUNT_ID.toString())
            .assertUserNameDisplayed()

        homeRobot
            .clickDiscoverTab()
            .assertTabSelected(HomeTestTags.DISCOVER_TAB)
        discoverRobot
            .assertFeaturedShowDisplayed(breakingBadTmdbId)
    }
}
