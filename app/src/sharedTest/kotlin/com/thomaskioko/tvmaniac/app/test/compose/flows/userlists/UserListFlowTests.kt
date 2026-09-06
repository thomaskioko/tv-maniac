package com.thomaskioko.tvmaniac.app.test.compose.flows.userlists

import com.thomaskioko.tvmaniac.app.test.AppFlowScope
import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import com.thomaskioko.tvmaniac.testing.integration.TEST_CREATED_LIST_NAME
import com.thomaskioko.tvmaniac.testing.integration.TEST_CREATED_LIST_TRAKT_ID
import com.thomaskioko.tvmaniac.testing.integration.TEST_PROFILE_SLUG
import com.thomaskioko.tvmaniac.testtags.home.HomeTestTags
import org.junit.Test

internal class UserListFlowTests : BaseAppFlowTest() {

    private val breakingBadTmdbId = 1396L
    private val favoritesListTraktId = 34223248L
    private val animeListTraktId = 34223402L
    private val favoritesListId = 1L
    private val animeListId = 2L
    private val createdListId = 3L
    private val firstLocalListId = 1L

    @Test
    fun givenNoSession_whenShowAddedToNewList_thenListIsCreatedWithoutLoginWall() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        openListSheet()

        showListRobot
            .assertSheetDisplayed()
            .clickCreateListButton()
            .assertCreateListFieldDisplayed()
            .typeCreateListName(SIGNED_OUT_LIST_NAME)
            .clickCreateListSubmit()
            .assertCreateListFieldDoesNotExist()
            .assertListItemDisplayed(firstLocalListId)
            .assertListSwitchIsUnchecked(firstLocalListId)
            .assertListShowCountText(firstLocalListId, "0 shows")
            .clickListSwitch(firstLocalListId)
            .assertListSwitchIsChecked(firstLocalListId)
            .assertListShowCountText(firstLocalListId, "1 show")
            .clickCloseSheetButton()
            .assertSheetDoesNotExist()
    }

    @Test
    fun givenSimklSession_whenShowAddedToNewList_thenListIsCreatedWithoutTraktSync() = runAppFlowTest {
        scenarios.flags.enableSimklLogin()
        scenarios.discover.stubBrowseGraph()
        scenarios.stubAuthenticatedSimklProfile()

        openListSheet()

        showListRobot
            .assertSheetDisplayed()
            .clickCreateListButton()
            .assertCreateListFieldDisplayed()
            .typeCreateListName(SIMKL_LIST_NAME)
            .clickCreateListSubmit()
            .assertCreateListFieldDoesNotExist()
            .assertListItemDisplayed(firstLocalListId)
            .assertListSwitchIsUnchecked(firstLocalListId)
            .assertListShowCountText(firstLocalListId, "0 shows")
            .clickListSwitch(firstLocalListId)
            .assertListSwitchIsChecked(firstLocalListId)
            .assertListShowCountText(firstLocalListId, "1 show")
            .clickCloseSheetButton()
            .assertSheetDoesNotExist()
    }

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

    @Test
    fun givenAuthenticatedUser_whenListOpenedFromProfile_thenShowIsRemovedWithConfirmation() = runAppFlowTest {
        scenarios.stubAuthenticatedSync()
        scenarios.traktLists.stubListItems(listId = favoritesListTraktId)
        scenarios.traktLists.stubRemoveShowFromList(listId = favoritesListTraktId)

        rootRobot.dismissNotificationRationale()

        homeRobot
            .clickProfileTab()
            .assertTabSelected(HomeTestTags.PROFILE_TAB)

        profileRobot
            .assertProfileScreenDisplayed()
            .scrollToUserLists(slug = TEST_PROFILE_SLUG)
            .assertListCardDisplayed(favoritesListId)
            .clickListCard(favoritesListId)

        listDetailRobot
            .assertListDetailScreenDisplayed()
            .assertShowCardDisplayed(breakingBadTmdbId)
            .longClickShowCard(breakingBadTmdbId)
            .assertRemoveConfirmationDisplayed()
            .clickRemoveConfirm()
            .assertShowCardDoesNotExist(breakingBadTmdbId)
            .clickBackButton()

        profileRobot
            .assertProfileScreenDisplayed()
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

    private companion object {
        private const val SIGNED_OUT_LIST_NAME = "Weekend Watch"
        private const val SIMKL_LIST_NAME = "Rewatch Queue"
    }
}
