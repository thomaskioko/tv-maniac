package com.thomaskioko.tvmaniac.app.test.compose.flows.discover

import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import com.thomaskioko.tvmaniac.testtags.seasondetails.SeasonDetailsTestTags
import org.junit.Test

internal class DiscoverToSeasonDetailsFlowTest : BaseAppFlowTest() {

    private val breakingBadTraktId = 1388L
    private val pilotEpisodeTraktId = 73640L
    private val secondEpisodeTraktId = 73641L
    private val seasonTwoFirstEpisodeTraktId = 73489L

    @Test
    fun discoverToSeasonDetailsJourney() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        // 1. Open Show Details & verify chips
        discoverRobot
            .assertFeaturedPagerDisplayed()
            .clickShowCard(breakingBadTraktId)

        // 2. Open Season Details
        showDetailsRobot
            .assertShowDetailsDisplayed()
            .assertSeasonChipDisplayed(seasonNumber = 1L)
            .assertSeasonChipDisplayed(seasonNumber = 2L)
            .clickSeasonChip(seasonNumber = 1L)

        seasonDetailsRobot
            .assertSeasonDetailsDisplayed()
            .assertEpisodeRowDisplayed(pilotEpisodeTraktId)
            .clickEpisodeHeader()
            .assertEpisodeRowDoesNotExist(pilotEpisodeTraktId)
            .clickEpisodeHeader()
            .assertEpisodeRowDisplayed(pilotEpisodeTraktId)
            .clickEpisodeRow(pilotEpisodeTraktId)

        episodeSheetRobot
            .assertEpisodeSheetDisplayed()
            .pressBack()

        seasonDetailsRobot
            .assertSeasonDetailsDisplayed()
            .clickBackButton()
            .assertDoesNotExist(SeasonDetailsTestTags.SCREEN_TEST_TAG)

        showDetailsRobot
            .assertShowDetailsDisplayed()
    }
}
