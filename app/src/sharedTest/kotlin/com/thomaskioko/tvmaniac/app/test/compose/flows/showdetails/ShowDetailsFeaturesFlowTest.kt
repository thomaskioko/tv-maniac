package com.thomaskioko.tvmaniac.app.test.compose.flows.showdetails

import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import com.thomaskioko.tvmaniac.testtags.home.HomeTestTags
import org.junit.Test

internal class ShowDetailsFeaturesFlowTest : BaseAppFlowTest() {

    private val breakingBadTmdbId = 1396L
    private val pilotEpisodeTraktId = 73640L
    private val favoritesListTraktId = 34223248L
    private val ratedStarValue = 8

    @Test
    fun givenShowDetails_whenOpened_thenInteractiveSurfacesAreExercised() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        discoverRobot
            .assertFeaturedPagerDisplayed()
            .clickShowCard(breakingBadTmdbId)

        showDetailsRobot
            .assertShowDetailsDisplayed()
            .assertDoesNotExist(HomeTestTags.NAVIGATION_BAR)
            .assertTrackButtonDisplayed()
            .clickTrackButton()

        rootRobot
            .dismissNotificationRationale()

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
    }

    @Test
    fun givenAuthenticatedUser_whenAddToListClicked_thenShowsListSheet() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        scenarios.stubAuthenticatedSync()

        rootRobot
            .dismissNotificationRationale()

        discoverRobot
            .assertFeaturedPagerDisplayed()
            .assertUpNextCardDisplayed(breakingBadTmdbId)
            .clickShowCard(breakingBadTmdbId)

        showDetailsRobot
            .assertShowDetailsDisplayed()
            .clickAddToListButton()

        showListRobot
            .assertSheetDisplayed()
            .assertTraktListItemDisplayed(favoritesListTraktId)
    }

    @Test
    fun givenAuthenticatedUser_whenShowRated_thenRatingPersistsAndCanBeCleared() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()
        scenarios.stubAuthenticatedSync()
        scenarios.ratings.stubRatingsSync()

        rootRobot.dismissNotificationRationale()

        discoverRobot
            .assertFeaturedPagerDisplayed()
            .clickShowCard(breakingBadTmdbId)

        showDetailsRobot
            .assertShowDetailsDisplayed()
            .clickRateButton()

        ratingSheetRobot
            .assertSheetDisplayed()
            .assertClearRatingButtonDoesNotExist()
            .clickStar(ratedStarValue)
            .assertClearRatingButtonDisplayed()
            .clickClearRatingButton()
            .assertClearRatingButtonDoesNotExist()
    }

    @Test
    fun givenSimklSession_whenShowDetailsOpened_thenAddToListButtonIsDisabled() = runAppFlowTest {
        scenarios.flags.enableSimklLogin()
        scenarios.discover.stubBrowseGraph()
        scenarios.stubAuthenticatedSimklProfile()

        discoverRobot
            .assertFeaturedPagerDisplayed()
            .clickShowCard(breakingBadTmdbId)

        showDetailsRobot
            .assertShowDetailsDisplayed()
            .assertAddToListButtonDisabled()
    }
}
