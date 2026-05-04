package com.thomaskioko.tvmaniac.app.test.compose.flows.library

import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import com.thomaskioko.tvmaniac.testtags.home.HomeTestTags
import org.junit.Test

internal class LibraryFlowTest : BaseAppFlowTest() {

    private val breakingBadTraktId = 1388L
    private val forAllMankindTraktId = 140481L
    private val theBoysTraktId = 139960L

    @Test
    fun givenLibrary_whenSearchQueryEntered_thenFiltersResults() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()
        scenarios.library.stubLibrarySyncEndpoints()

        discoverRobot.assertDiscoverScreenDisplayed()

        scenarios.signInAndDismissRationale()

        homeRobot.clickLibraryTab()
            .assertTabSelected(HomeTestTags.LIBRARY_TAB)

        libraryRobot
            .scrollToShowRow(breakingBadTraktId)
            .assertShowRowDisplayed(breakingBadTraktId)
            .scrollToShowRow(forAllMankindTraktId)
            .assertShowRowDisplayed(forAllMankindTraktId)
            .scrollToShowRow(theBoysTraktId)
            .assertShowRowDisplayed(theBoysTraktId)

        rootRobot
            .assertNoSnackbarDisplayed()

        libraryRobot
            .clickSearchButton()
            .enterSearchQuery("Breaking")
            .scrollToShowRow(breakingBadTraktId)
            .assertShowRowDisplayed(breakingBadTraktId)
            .enterSearchQuery("NonExistent")
            .assertEmptyStateDisplayed()
    }

    @Test
    fun givenLibrary_whenGenreSelected_thenFiltersResults() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()
        scenarios.library.stubLibrarySyncEndpoints()

        discoverRobot
            .assertDiscoverScreenDisplayed()

        scenarios.signInAndDismissRationale()

        homeRobot
            .clickLibraryTab()
            .assertTabSelected(HomeTestTags.LIBRARY_TAB)

        libraryRobot
            .scrollToShowRow(breakingBadTraktId)
            .assertShowRowDisplayed(breakingBadTraktId)
            .scrollToShowRow(forAllMankindTraktId)
            .assertShowRowDisplayed(forAllMankindTraktId)
            .scrollToShowRow(theBoysTraktId)
            .assertShowRowDisplayed(theBoysTraktId)

        rootRobot
            .assertNoSnackbarDisplayed()

        libraryRobot
            .clickFilterButton()
            .selectGenreFilter("Drama")
            .clickApplyFilter()
            .scrollToShowRow(breakingBadTraktId)
            .assertShowRowDisplayed(breakingBadTraktId)
    }
}
