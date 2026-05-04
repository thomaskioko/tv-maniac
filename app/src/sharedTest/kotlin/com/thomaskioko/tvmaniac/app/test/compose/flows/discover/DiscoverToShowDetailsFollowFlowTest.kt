package com.thomaskioko.tvmaniac.app.test.compose.flows.discover

import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import com.thomaskioko.tvmaniac.testtags.showdetails.ShowDetailsTestTags
import org.junit.Test

internal class DiscoverToShowDetailsFollowFlowTest : BaseAppFlowTest() {

    private val breakingBadTraktId = 1388L

    @Test
    fun givenShow_whenTrackIsClicked_thenPersistsInFollowedShows() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        discoverRobot
            .assertLoadingIndicatorDisplayed()
            .assertFeaturedPagerDisplayed()
            .clickShowCard(breakingBadTraktId)

        trackShow()
    }

    @Test
    fun givenShowDetails_whenBackIsPressed_thenRestoresDiscover() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        discoverRobot
            .assertFeaturedPagerDisplayed()
            .clickShowCard(breakingBadTraktId)
            .assertShowDetailsDisplayed()
            .pressBack()

        showDetailsRobot
            .assertDoesNotExist(ShowDetailsTestTags.SHOW_DETAILS_SCREEN_TEST_TAG)

        discoverRobot
            .assertShowCardDisplayed(breakingBadTraktId)
    }
}
