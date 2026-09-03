package com.thomaskioko.tvmaniac.app.test.compose.flows.deeplink

import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import org.junit.Test

internal class DeepLinkToShowDetailsFlowTest : BaseAppFlowTest() {

    private val breakingBadTmdbId = 1396L

    @Test
    fun deepLinkToShowDetailsJourney() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        discoverRobot.assertFeaturedPagerDisplayed()

        activityGraph.rootPresenter.onDeepLinkUrl("tvmaniac://show/$breakingBadTmdbId")

        showDetailsRobot
            .waitForIdle()
            .assertShowDetailsDisplayed()
    }

    @Test
    fun unknownDeepLinkLeavesDiscoverShowing() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        discoverRobot.assertFeaturedPagerDisplayed()

        activityGraph.rootPresenter.onDeepLinkUrl("tvmaniac://show/not-a-number")

        discoverRobot
            .waitForIdle()
            .assertFeaturedPagerDisplayed()
    }
}
