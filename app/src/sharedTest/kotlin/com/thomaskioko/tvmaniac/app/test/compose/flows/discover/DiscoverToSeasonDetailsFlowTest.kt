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
        discoverRobot.clickShowCard(breakingBadTraktId)
        showDetailsRobot.assertShowDetailsDisplayed()
        showDetailsRobot.assertSeasonChipDisplayed(seasonNumber = 1L)
        showDetailsRobot.assertSeasonChipDisplayed(seasonNumber = 2L)

        // 2. Open Season Details
        showDetailsRobot.clickSeasonChip(seasonNumber = 1L)
        seasonDetailsRobot.assertSeasonDetailsDisplayed()
        seasonDetailsRobot.assertEpisodeRowDisplayed(pilotEpisodeTraktId)

        // 3. Toggle Episode List
        seasonDetailsRobot.clickEpisodeHeader()
        seasonDetailsRobot.assertEpisodeRowDoesNotExist(pilotEpisodeTraktId)
        seasonDetailsRobot.clickEpisodeHeader()
        seasonDetailsRobot.assertEpisodeRowDisplayed(pilotEpisodeTraktId)

        // 4. Open Episode Sheet
        seasonDetailsRobot.clickEpisodeRow(pilotEpisodeTraktId)
        episodeSheetRobot.assertEpisodeSheetDisplayed()

        // 5. Back to Season Details (Dismiss Sheet)
        episodeSheetRobot.pressBack()
        seasonDetailsRobot.assertSeasonDetailsDisplayed()

        // 6. Back to Show Details
        seasonDetailsRobot.clickBackButton()
        seasonDetailsRobot.assertDoesNotExist(SeasonDetailsTestTags.SCREEN_TEST_TAG)
        showDetailsRobot.assertShowDetailsDisplayed()
    }
}
