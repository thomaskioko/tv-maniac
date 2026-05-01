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
    fun givenLibrary_whenSearchQueryEntered_thenFiltersResults() {
        discoverRobot.assertDiscoverScreenDisplayed()

        scenarios.signInAndDismissRationale()

        homeRobot.clickLibraryTab()

        libraryRobot.assertShowRowDisplayed(breakingBadTraktId)
        libraryRobot.assertShowRowDisplayed(forAllMankindTraktId)
        libraryRobot.assertShowRowDisplayed(theBoysTraktId)
        rootRobot.assertNoSnackbarDisplayed()

        libraryRobot.clickSearchButton()
        libraryRobot.enterSearchQuery("Breaking")
        libraryRobot.assertShowRowDisplayed(breakingBadTraktId)

        libraryRobot.enterSearchQuery("NonExistent")
        libraryRobot.assertEmptyStateDisplayed()
    }

    @Test
    fun givenLibrary_whenGenreSelected_thenFiltersResults() {
        discoverRobot.assertDiscoverScreenDisplayed()

        scenarios.signInAndDismissRationale()

        homeRobot.clickLibraryTab()

        libraryRobot.assertShowRowDisplayed(breakingBadTraktId)
        libraryRobot.assertShowRowDisplayed(forAllMankindTraktId)
        libraryRobot.assertShowRowDisplayed(theBoysTraktId)
        rootRobot.assertNoSnackbarDisplayed()

        libraryRobot.clickFilterButton()
        libraryRobot.selectGenreFilter("Drama")
        libraryRobot.clickApplyFilter()

        libraryRobot.assertShowRowDisplayed(breakingBadTraktId)
    }
}
