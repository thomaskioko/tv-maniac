package com.thomaskioko.tvmaniac.app.test.compose.flows.userlists

import com.thomaskioko.tvmaniac.app.test.AppFlowScope
import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import com.thomaskioko.tvmaniac.app.test.compose.stubs.TEST_CREATED_LIST_NAME
import com.thomaskioko.tvmaniac.app.test.compose.stubs.TEST_CREATED_LIST_TRAKT_ID
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

        showDetailsRobot
            .assertListSheetDisplayed()
            .assertTraktListItemDisplayed(favoritesListTraktId)
            .assertTraktListItemDisplayed(animeListTraktId)
            .assertListSwitchIsUnchecked(favoritesListTraktId)
            .assertListSwitchIsUnchecked(animeListTraktId)
            .assertTraktListShowCountText(favoritesListTraktId, "0 shows")
            .assertTraktListShowCountText(animeListTraktId, "0 shows")
            // 2. Add to multiple lists & verify counts
            .clickListSwitch(favoritesListTraktId)
            .assertListSwitchIsChecked(favoritesListTraktId)
            .assertTraktListShowCountText(favoritesListTraktId, "1 show")
            .clickListSwitch(animeListTraktId)
            .assertListSwitchIsChecked(animeListTraktId)
            .assertTraktListShowCountText(animeListTraktId, "1 show")
            // 3. Create new list
            .assertCreateListFieldDoesNotExist()
            .clickCreateListButton()
            .assertCreateListFieldDisplayed()
            .typeCreateListName(TEST_CREATED_LIST_NAME)
            .clickCreateListSubmit()
            .assertCreateListFieldDoesNotExist()
            .assertTraktListItemDisplayed(TEST_CREATED_LIST_TRAKT_ID)
            .assertListSwitchIsUnchecked(TEST_CREATED_LIST_TRAKT_ID)
            .assertTraktListShowCountText(TEST_CREATED_LIST_TRAKT_ID, "0 shows")
            .clickListSwitch(TEST_CREATED_LIST_TRAKT_ID)
            .assertListSwitchIsChecked(TEST_CREATED_LIST_TRAKT_ID)
            .assertTraktListShowCountText(TEST_CREATED_LIST_TRAKT_ID, "1 show")
            .clickCloseListSheetButton()
            .assertListSheetDoesNotExist()
    }

    private fun AppFlowScope.openListSheet() {
        rootRobot.dismissNotificationRationale()

        discoverRobot
            .assertFeaturedPagerDisplayed()
            .clickShowCard(breakingBadTraktId)

        showDetailsRobot
            .assertShowDetailsDisplayed()
            .clickAddToListButton()
            .assertListSheetDisplayed()
    }
}
