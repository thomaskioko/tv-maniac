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
    fun shouldShowListSheetWhenAddToListClicked() {
        openListSheet()

        showDetailsRobot.verifyListSheetIsShown()
        showDetailsRobot.verifyTraktListItemIsShown(favoritesListTraktId)
        showDetailsRobot.verifyTraktListItemIsShown(animeListTraktId)
    }

    @Test
    fun shouldAddShowToMultipleListsAndUpdateShowCounts() {
        scenarios.traktLists.stubAddShowToList(listId = favoritesListTraktId)
        scenarios.traktLists.stubAddShowToList(listId = animeListTraktId)

        openListSheet()
        showDetailsRobot.verifyListSwitchIsUnchecked(favoritesListTraktId)
        showDetailsRobot.verifyListSwitchIsUnchecked(animeListTraktId)
        showDetailsRobot.verifyTraktListShowCountText(favoritesListTraktId, "0 shows")
        showDetailsRobot.verifyTraktListShowCountText(animeListTraktId, "0 shows")

        showDetailsRobot.clickListSwitch(favoritesListTraktId)
        showDetailsRobot.verifyListSwitchIsChecked(favoritesListTraktId)
        showDetailsRobot.verifyTraktListShowCountText(favoritesListTraktId, "1 show")

        showDetailsRobot.clickListSwitch(animeListTraktId)
        showDetailsRobot.verifyListSwitchIsChecked(animeListTraktId)
        showDetailsRobot.verifyTraktListShowCountText(animeListTraktId, "1 show")
    }

    @Test
    fun shouldRevealCreateListFieldWhenCreateButtonTapped() {
        openListSheet()
        showDetailsRobot.verifyCreateListFieldIsHidden()

        showDetailsRobot.clickCreateListButton()

        showDetailsRobot.verifyCreateListFieldIsShown()
    }

    @Test
    fun shouldAppendNewListAndAddShowToItWhenCreateSubmitted() {
        scenarios.traktLists.stubCreateList()
        scenarios.traktLists.stubAddShowToList(listId = TEST_CREATED_LIST_TRAKT_ID)

        openListSheet()
        showDetailsRobot.clickCreateListButton()
        showDetailsRobot.verifyCreateListFieldIsShown()

        showDetailsRobot.typeCreateListName(TEST_CREATED_LIST_NAME)
        showDetailsRobot.clickCreateListSubmit()

        showDetailsRobot.verifyCreateListFieldIsHidden()
        showDetailsRobot.verifyTraktListItemIsShown(TEST_CREATED_LIST_TRAKT_ID)
        showDetailsRobot.verifyListSwitchIsUnchecked(TEST_CREATED_LIST_TRAKT_ID)
        showDetailsRobot.verifyTraktListShowCountText(TEST_CREATED_LIST_TRAKT_ID, "0 shows")

        showDetailsRobot.clickListSwitch(TEST_CREATED_LIST_TRAKT_ID)

        showDetailsRobot.verifyListSwitchIsChecked(TEST_CREATED_LIST_TRAKT_ID)
        showDetailsRobot.verifyTraktListShowCountText(TEST_CREATED_LIST_TRAKT_ID, "1 show")
    }

    @Test
    fun shouldDismissSheetWhenCloseButtonTapped() {
        openListSheet()
        showDetailsRobot.verifyListSheetIsShown()

        showDetailsRobot.clickCloseListSheetButton()

        showDetailsRobot.verifyListSheetIsHidden()
    }

    private fun openListSheet() {
        rootRobot.verifyNotificationRationaleIsShownAndDismissed()

        discoverRobot.clickShowCard(breakingBadTraktId)
        showDetailsRobot.verifyShowDetailsIsShown()

        showDetailsRobot.scrollToListTag(
            listTag = ShowDetailsTestTags.SHOW_DETAILS_SCREEN_TEST_TAG,
            itemTag = ShowDetailsTestTags.ADD_TO_LIST_BUTTON_TEST_TAG,
        )
        showDetailsRobot.clickAddToListButton()
        showDetailsRobot.verifyListSheetIsShown()
    }
}
