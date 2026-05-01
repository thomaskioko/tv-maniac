package com.thomaskioko.tvmaniac.app.test.compose.flows.userlists

import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import com.thomaskioko.tvmaniac.app.test.compose.stubs.TEST_CREATED_LIST_NAME
import com.thomaskioko.tvmaniac.app.test.compose.stubs.TEST_CREATED_LIST_TRAKT_ID
import com.thomaskioko.tvmaniac.testtags.showdetails.ShowDetailsTestTags
import org.junit.Test

internal class UserListFlowTests : BaseAppFlowTest() {

    private val breakingBadTraktId = 1388L
    private val favoritesListTraktId = 34223248L
    private val animeListTraktId = 34223402L

    override fun onBeforeTest() {
        super.onBeforeTest()
        scenarios.stubAuthenticatedSync()
    }

    @Test
    fun givenAuthenticatedUser_whenAddToListClicked_thenShowsListSheet() {
        openListSheet()

        showDetailsRobot.assertListSheetDisplayed()
        showDetailsRobot.assertTraktListItemDisplayed(favoritesListTraktId)
        showDetailsRobot.assertTraktListItemDisplayed(animeListTraktId)
    }

    @Test
    fun givenListSheet_whenShowAddedToMultipleLists_thenUpdatesShowCounts() {
        scenarios.traktLists.stubAddShowToList(listId = favoritesListTraktId)
        scenarios.traktLists.stubAddShowToList(listId = animeListTraktId)

        openListSheet()
        showDetailsRobot.assertListSwitchIsUnchecked(favoritesListTraktId)
        showDetailsRobot.assertListSwitchIsUnchecked(animeListTraktId)
        showDetailsRobot.assertTraktListShowCountText(favoritesListTraktId, "0 shows")
        showDetailsRobot.assertTraktListShowCountText(animeListTraktId, "0 shows")

        showDetailsRobot.clickListSwitch(favoritesListTraktId)
        showDetailsRobot.assertListSwitchIsChecked(favoritesListTraktId)
        showDetailsRobot.assertTraktListShowCountText(favoritesListTraktId, "1 show")

        showDetailsRobot.clickListSwitch(animeListTraktId)
        showDetailsRobot.assertListSwitchIsChecked(animeListTraktId)
        showDetailsRobot.assertTraktListShowCountText(animeListTraktId, "1 show")
    }

    @Test
    fun givenListSheet_whenCreateClicked_thenShowsCreateListField() {
        openListSheet()
        showDetailsRobot.assertCreateListFieldDoesNotExist()

        showDetailsRobot.clickCreateListButton()

        showDetailsRobot.assertCreateListFieldDisplayed()
    }

    @Test
    fun givenListSheet_whenNewListCreated_thenAddsShowToList() {
        scenarios.traktLists.stubCreateList()
        scenarios.traktLists.stubAddShowToList(listId = TEST_CREATED_LIST_TRAKT_ID)

        openListSheet()
        showDetailsRobot.clickCreateListButton()
        showDetailsRobot.assertCreateListFieldDisplayed()

        showDetailsRobot.typeCreateListName(TEST_CREATED_LIST_NAME)
        showDetailsRobot.clickCreateListSubmit()

        showDetailsRobot.assertCreateListFieldDoesNotExist()
        showDetailsRobot.assertTraktListItemDisplayed(TEST_CREATED_LIST_TRAKT_ID)
        showDetailsRobot.assertListSwitchIsUnchecked(TEST_CREATED_LIST_TRAKT_ID)
        showDetailsRobot.assertTraktListShowCountText(TEST_CREATED_LIST_TRAKT_ID, "0 shows")

        showDetailsRobot.clickListSwitch(TEST_CREATED_LIST_TRAKT_ID)

        showDetailsRobot.assertListSwitchIsChecked(TEST_CREATED_LIST_TRAKT_ID)
        showDetailsRobot.assertTraktListShowCountText(TEST_CREATED_LIST_TRAKT_ID, "1 show")
    }

    @Test
    fun givenListSheet_whenCloseClicked_thenDismissesSheet() {
        openListSheet()
        showDetailsRobot.assertListSheetDisplayed()

        showDetailsRobot.clickCloseListSheetButton()

        showDetailsRobot.assertListSheetDoesNotExist()
    }

    private fun openListSheet() {
        rootRobot.dismissNotificationRationale()

        discoverRobot.clickShowCard(breakingBadTraktId)
        showDetailsRobot.assertShowDetailsDisplayed()

        showDetailsRobot.scrollToListTag(
            listTag = ShowDetailsTestTags.SHOW_DETAILS_SCREEN_TEST_TAG,
            itemTag = ShowDetailsTestTags.ADD_TO_LIST_BUTTON_TEST_TAG,
        )
        showDetailsRobot.clickAddToListButton()
        showDetailsRobot.assertListSheetDisplayed()
    }
}
