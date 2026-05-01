package com.thomaskioko.tvmaniac.app.test.compose.flows.showdetails

import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import com.thomaskioko.tvmaniac.testtags.showdetails.ShowDetailsTestTags
import org.junit.Test

internal class ShowDetailsFeaturesFlowTest : BaseAppFlowTest() {

    private val breakingBadTraktId = 1388L
    private val pilotEpisodeTraktId = 73640L
    private val favoritesListTraktId = 34223248L

    @Test
    fun givenShowDetails_whenOpened_thenInteractiveSurfacesAreExercised() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        discoverRobot.clickShowCard(breakingBadTraktId)
        showDetailsRobot.assertShowDetailsDisplayed()

        // Track show
        showDetailsRobot.assertTrackButtonDisplayed()
        showDetailsRobot.clickTrackButton()

        rootRobot.dismissNotificationRationale()

        showDetailsRobot.assertStopTrackingButtonDisplayed()

        showDetailsRobot.assertContinueTrackingSectionDisplayed()

        showDetailsRobot.clickContinueTrackingMarkWatched(pilotEpisodeTraktId)

        // Navigate to season details and back
        showDetailsRobot.clickSeasonChip(seasonNumber = 1L)
        seasonDetailsRobot.assertSeasonDetailsDisplayed()
        seasonDetailsRobot.pressBack()

        // Verify additional sections
        showDetailsRobot.scrollTo(ShowDetailsTestTags.CAST_LIST_TEST_TAG)
        showDetailsRobot.assertCastListDisplayed()

        showDetailsRobot.scrollTo(ShowDetailsTestTags.TRAILERS_LIST_TEST_TAG)
        showDetailsRobot.assertTrailersListDisplayed()

        showDetailsRobot.scrollTo(ShowDetailsTestTags.SIMILAR_SHOWS_LIST_TEST_TAG)
        showDetailsRobot.assertSimilarShowsListDisplayed()

        // Verify login prompt for logged-out users when adding to list
        showDetailsRobot.scrollToListTag(
            listTag = ShowDetailsTestTags.SHOW_DETAILS_SCREEN_TEST_TAG,
            itemTag = ShowDetailsTestTags.ADD_TO_LIST_BUTTON_TEST_TAG,
        )
        showDetailsRobot.clickAddToListButton()
        showDetailsRobot.assertLoginPromptDisplayed()
    }

    @Test
    fun givenAuthenticatedUser_whenAddToListClicked_thenShowsListSheet() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        scenarios.stubAuthenticatedSync()

        rootRobot.dismissNotificationRationale()

        discoverRobot.clickShowCard(breakingBadTraktId)
        showDetailsRobot.assertShowDetailsDisplayed()

        showDetailsRobot.scrollToListTag(
            listTag = ShowDetailsTestTags.SHOW_DETAILS_SCREEN_TEST_TAG,
            itemTag = ShowDetailsTestTags.ADD_TO_LIST_BUTTON_TEST_TAG,
        )
        showDetailsRobot.clickAddToListButton()

        showDetailsRobot.assertLoginPromptDoesNotExist()
        showDetailsRobot.assertListSheetDisplayed()
        showDetailsRobot.assertTraktListItemDisplayed(favoritesListTraktId)
    }
}
