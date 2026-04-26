package com.thomaskioko.tvmaniac.app.test.compose.flows.home

import com.thomaskioko.tvmaniac.app.test.util.BaseAppFlowTest
import com.thomaskioko.tvmaniac.home.nav.di.model.HomeConfig
import org.junit.Before
import org.junit.Test

internal class HomeTabsNavigationSmokeTest : BaseAppFlowTest() {

    @Before
    fun stubEndpoints() {
        scenarios.stubDiscoverBrowse()
    }

    @Test
    fun shouldOpenCorrectScreenWhenEachTabIsTapped() {
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
    fun shouldReflectActiveTabInSelectionState() {
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
