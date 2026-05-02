package com.thomaskioko.tvmaniac.app.test.compose.flows.userlists

import com.thomaskioko.tvmaniac.app.test.AppFlowScope
import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import com.thomaskioko.tvmaniac.app.test.compose.stubs.TEST_CREATED_LIST_NAME
import com.thomaskioko.tvmaniac.app.test.compose.stubs.TEST_CREATED_LIST_TRAKT_ID
import com.thomaskioko.tvmaniac.testtags.showdetails.ShowDetailsTestTags
import org.junit.Test

internal class UserListFlowTests : BaseAppFlowTest() {

    private val breakingBadTraktId = 1388L
    private val favoritesListTraktId = 34223248L
    private val animeListTraktId = 34223402L

    @Test
    fun userListManagementJourney() = runAppFlowTest {
        scenarios.stubAuthenticatedSync()
        scenarios.traktLists.stubAddShowToList(listId = favoritesListTraktId)
        scenarios.traktLists.stubAddShowToList(listId = animeListTraktId)
        scenarios.traktLists.stubCreateList()
        scenarios.traktLists.stubAddShowToList(listId = TEST_CREATED_LIST_TRAKT_ID)

        // 1. Open list sheet & verify initial state
        openListSheet()
        showDetailsRobot.assertListSheetDisplayed()
        showDetailsRobot.assertTraktListItemDisplayed(favoritesListTraktId)
        showDetailsRobot.assertTraktListItemDisplayed(animeListTraktId)
        showDetailsRobot.assertListSwitchIsUnchecked(favoritesListTraktId)
        showDetailsRobot.assertListSwitchIsUnchecked(animeListTraktId)
        showDetailsRobot.assertTraktListShowCountText(favoritesListTraktId, "0 shows")
        showDetailsRobot.assertTraktListShowCountText(animeListTraktId, "0 shows")

        // 2. Add to multiple lists & verify counts
        showDetailsRobot.clickListSwitch(favoritesListTraktId)
        showDetailsRobot.assertListSwitchIsChecked(favoritesListTraktId)
        showDetailsRobot.assertTraktListShowCountText(favoritesListTraktId, "1 show")

        showDetailsRobot.clickListSwitch(animeListTraktId)
        showDetailsRobot.assertListSwitchIsChecked(animeListTraktId)
        showDetailsRobot.assertTraktListShowCountText(animeListTraktId, "1 show")

        // 3. Create new list
        showDetailsRobot.assertCreateListFieldDoesNotExist()
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

        // 4. Close sheet
        showDetailsRobot.clickCloseListSheetButton()
        showDetailsRobot.assertListSheetDoesNotExist()
    }

    private fun AppFlowScope.openListSheet() {
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
