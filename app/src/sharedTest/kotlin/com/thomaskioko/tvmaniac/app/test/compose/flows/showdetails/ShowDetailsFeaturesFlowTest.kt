package com.thomaskioko.tvmaniac.app.test.compose.flows.showdetails

import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import org.junit.Test

internal class ShowDetailsFeaturesFlowTest : BaseAppFlowTest() {

    private val breakingBadTraktId = 1388L
    private val pilotEpisodeTraktId = 73640L
    private val favoritesListTraktId = 34223248L

    @Test
    fun givenShowDetails_whenOpened_thenInteractiveSurfacesAreExercised() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        discoverRobot
            .assertFeaturedPagerDisplayed()
            .clickShowCard(breakingBadTraktId)

        showDetailsRobot
            .assertShowDetailsDisplayed()
            .assertTrackButtonDisplayed()
            .clickTrackButton()

        rootRobot.dismissNotificationRationale()

        showDetailsRobot
            .assertStopTrackingButtonDisplayed()
            .assertContinueTrackingSectionDisplayed()
            .clickContinueTrackingMarkWatched(pilotEpisodeTraktId)
            // Navigate to season details and back
            .clickSeasonChip(seasonNumber = 1L)
            .assertSeasonDetailsDisplayed()
            .clickBackButton()

        showDetailsRobot
            .assertCastListDisplayed()
            .assertTrailersListDisplayed()
            .assertSimilarShowsListDisplayed()
            // Verify login prompt for logged-out users when adding to list
            .clickAddToListButton()
            .assertLoginPromptDisplayed()
    }

    @Test
    fun givenAuthenticatedUser_whenAddToListClicked_thenShowsListSheet() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        scenarios.stubAuthenticatedSync()

        rootRobot
            .dismissNotificationRationale()

        discoverRobot
            .assertFeaturedPagerDisplayed()
            .clickShowCard(breakingBadTraktId)

        showDetailsRobot
            .assertShowDetailsDisplayed()
            .clickAddToListButton()
            .assertListSheetDisplayed()
            .assertTraktListItemDisplayed(favoritesListTraktId)
    }
}
