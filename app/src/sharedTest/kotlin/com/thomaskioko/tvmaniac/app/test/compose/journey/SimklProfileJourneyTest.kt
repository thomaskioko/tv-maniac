package com.thomaskioko.tvmaniac.app.test.compose.journey

import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import com.thomaskioko.tvmaniac.app.test.compose.stubs.TEST_SIMKL_ACCOUNT_ID
import com.thomaskioko.tvmaniac.testtags.home.HomeTestTags
import org.junit.Test

internal class SimklProfileJourneyTest : BaseAppFlowTest() {

    @Test
    fun givenSimklSession_whenNavigatesToProfile_thenSyncedSectionsDisplayed() = runAppFlowTest {
        scenarios.stubUnauthenticatedState()
        scenarios.stubAuthenticatedSimklProfile()

        homeRobot
            .clickProfileTab()
            .assertTabSelected(HomeTestTags.PROFILE_TAB)

        profileRobot
            .assertUserCardDisplayed(slug = TEST_SIMKL_ACCOUNT_ID.toString())
            .assertUserNameDisplayed()
            .assertStatsCardDisplayed()
            .scrollToRecentlyWatched(slug = TEST_SIMKL_ACCOUNT_ID.toString())
            .assertRecentlyWatchedSectionDisplayed()
            .scrollToProgressSection(slug = TEST_SIMKL_ACCOUNT_ID.toString())
            .selectInProgressFilter()
            .assertInProgressShowDisplayed()
    }
}
