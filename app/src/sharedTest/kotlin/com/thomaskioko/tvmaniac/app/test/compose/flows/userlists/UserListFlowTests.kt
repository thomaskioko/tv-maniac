package com.thomaskioko.tvmaniac.app.test.compose.flows.userlists

import com.thomaskioko.tvmaniac.app.test.AppFlowScope
import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import com.thomaskioko.tvmaniac.testing.integration.TEST_CREATED_LIST_NAME
import com.thomaskioko.tvmaniac.testing.integration.TEST_CREATED_LIST_TRAKT_ID
import com.thomaskioko.tvmaniac.testtags.home.HomeTestTags
import org.junit.Test

internal class UserListFlowTests : BaseAppFlowTest() {

    private val breakingBadTmdbId = 1396L
    private val favoritesListTraktId = 34223248L
    private val animeListTraktId = 34223402L
    private val favoritesListId = 1L
    private val animeListId = 2L
    private val createdListId = 3L

    @Test
    fun userListManagementJourney() = runAppFlowTest {
        scenarios.stubAuthenticatedSync()
        scenarios.traktLists.stubAddShowToList(listId = favoritesListTraktId)
        scenarios.traktLists.stubAddShowToList(listId = animeListTraktId)
        scenarios.traktLists.stubCreateList()
        scenarios.traktLists.stubAddShowToList(listId = TEST_CREATED_LIST_TRAKT_ID)

        // 1. Open list sheet & verify initial state
        openListSheet()

        showListRobot
            .assertSheetDisplayed()
            .assertListItemDisplayed(favoritesListId)
            .assertListItemDisplayed(animeListId)
            .assertListSwitchIsUnchecked(favoritesListId)
            .assertListSwitchIsUnchecked(animeListId)
            .assertListShowCountText(favoritesListId, "0 shows")
            .assertListShowCountText(animeListId, "0 shows")
            // 2. Add to multiple lists & verify counts
            .clickListSwitch(favoritesListId)
            .assertListSwitchIsChecked(favoritesListId)
            .assertListShowCountText(favoritesListId, "1 show")
            .clickListSwitch(animeListId)
            .assertListSwitchIsChecked(animeListId)
            .assertListShowCountText(animeListId, "1 show")
            // 3. Create new list
            .assertCreateListFieldDoesNotExist()
            .clickCreateListButton()
            .assertCreateListFieldDisplayed()
            .typeCreateListName(TEST_CREATED_LIST_NAME)
            .clickCreateListSubmit()
            .assertCreateListFieldDoesNotExist()
            .assertListItemDisplayed(createdListId)
            .assertListSwitchIsUnchecked(createdListId)
            .assertListShowCountText(createdListId, "0 shows")
            .clickListSwitch(createdListId)
            .assertListSwitchIsChecked(createdListId)
            .assertListShowCountText(createdListId, "1 show")
            .clickCloseSheetButton()
            .assertSheetDoesNotExist()
    }

    private fun AppFlowScope.openListSheet() {
        rootRobot.dismissNotificationRationale()

        discoverRobot
            .assertFeaturedPagerDisplayed()
            .clickShowCard(breakingBadTmdbId)

        showDetailsRobot
            .assertShowDetailsDisplayed()
            .assertDoesNotExist(HomeTestTags.NAVIGATION_BAR)
            .clickAddToListButton()

        showListRobot.assertSheetDisplayed()
    }
}
