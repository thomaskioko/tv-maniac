package com.thomaskioko.tvmaniac.app.test.compose.flows.home

import com.thomaskioko.tvmaniac.app.test.util.BaseAppRobolectricTest
import com.thomaskioko.tvmaniac.home.nav.di.model.HomeConfig
import org.junit.Before
import kotlin.test.Test

internal class HomeTabsNavigationSmokeTest : BaseAppRobolectricTest() {

    @Before
    fun stubEndpoints() {
        scenarios.stubDiscoverBrowse()
    }

    @Test
    fun `should open correct screen when each tab is tapped`() {
        discoverRobot.verifyDiscoverScreenIsShown()

        homeRobot.clickProgressTab()
        progressRobot.verifyProgressScreenIsShown()

        homeRobot.clickLibraryTab()
        libraryRobot.verifyLibraryScreenIsShown()

        homeRobot.clickProfileTab()
        profileRobot.verifyProfileScreenIsShown()

        homeRobot.clickDiscoverTab()
        discoverRobot.verifyDiscoverScreenIsShown()
    }

    @Test
    fun `should reflect active tab in selection state`() {
        homeRobot.verifyTabSelected(HomeConfig.Discover)
        homeRobot.verifyTabNotSelected(HomeConfig.Progress)

        homeRobot.clickProgressTab()
        homeRobot.verifyTabSelected(HomeConfig.Progress)
        homeRobot.verifyTabNotSelected(HomeConfig.Discover)

        homeRobot.clickLibraryTab()
        homeRobot.verifyTabSelected(HomeConfig.Library)
        homeRobot.verifyTabNotSelected(HomeConfig.Progress)
    }
}
