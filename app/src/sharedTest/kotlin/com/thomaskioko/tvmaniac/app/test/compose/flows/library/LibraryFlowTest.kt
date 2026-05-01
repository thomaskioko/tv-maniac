package com.thomaskioko.tvmaniac.app.test.compose.flows.library

import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import org.junit.Test

internal class LibraryFlowTest : BaseAppFlowTest() {

    private val breakingBadTraktId = 1388L
    private val forAllMankindTraktId = 140481L
    private val theBoysTraktId = 139960L

    override fun onBeforeTest() {
        super.onBeforeTest()
        scenarios.discover.stubBrowseGraph()
        scenarios.library.stubLibrarySyncEndpoints()
    }

    @Test
    fun shouldFilterLibraryBySearchQuery() {
        discoverRobot.verifyDiscoverScreenIsShown()

        scenarios.signInAndDismissRationale()

        homeRobot.clickLibraryTab()

        libraryRobot.verifyShowRowIsShown(breakingBadTraktId)
        libraryRobot.verifyShowRowIsShown(forAllMankindTraktId)
        libraryRobot.verifyShowRowIsShown(theBoysTraktId)
        rootRobot.verifyNoSnackbarShown()

        libraryRobot.clickSearchButton()
        libraryRobot.enterSearchQuery("Breaking")
        libraryRobot.verifyShowRowIsShown(breakingBadTraktId)

        libraryRobot.enterSearchQuery("NonExistent")
        libraryRobot.verifyEmptyStateIsShown()
    }

    @Test
    fun shouldFilterLibraryByGenre() {
        discoverRobot.verifyDiscoverScreenIsShown()

        scenarios.signInAndDismissRationale()

        homeRobot.clickLibraryTab()

        libraryRobot.verifyShowRowIsShown(breakingBadTraktId)
        libraryRobot.verifyShowRowIsShown(forAllMankindTraktId)
        libraryRobot.verifyShowRowIsShown(theBoysTraktId)
        rootRobot.verifyNoSnackbarShown()

        libraryRobot.clickFilterButton()
        libraryRobot.selectGenreFilter("Drama")
        libraryRobot.clickApplyFilter()

        libraryRobot.verifyShowRowIsShown(breakingBadTraktId)
    }
}
