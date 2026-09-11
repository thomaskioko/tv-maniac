package com.thomaskioko.tvmaniac.app.test.compose.flows.reconnect

import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import com.thomaskioko.tvmaniac.testing.integration.Endpoints
import com.thomaskioko.tvmaniac.testing.integration.MockEngineHandler
import com.thomaskioko.tvmaniac.testing.integration.util.FixtureLoader
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import org.junit.Test

internal class ReconnectSyncFlowTest : BaseAppFlowTest() {

    private val breakingBadTmdbId = 1396L

    private val nextEpisodeTraktId = 73641L

    @Test
    fun givenEpisodeMarkedWatchedOffline_whenReconnected_thenTraktHistoryIsPushed() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()
        scenarios.stubAuthenticatedSync()

        graph.initializers.initialize()

        var syncHistoryPostCount = 0
        MockEngineHandler.handler.stub(
            method = HttpMethod.Post,
            path = Endpoints.Trakt.SyncHistory.path,
            host = Endpoints.Trakt.SyncHistory.host,
        ) { _ ->
            syncHistoryPostCount++
            respond(
                content = FixtureLoader.load(Endpoints.Trakt.SyncHistory.successFixture),
                status = HttpStatusCode.OK,
                headers = MockEngineHandler.handler.jsonHeaders,
            )
        }

        rootRobot.dismissNotificationRationale()

        discoverRobot
            .assertFeaturedPagerDisplayed()
            .clickShowCard(breakingBadTmdbId)

        showDetailsRobot
            .assertShowDetailsDisplayed()
            .assertStopTrackingButtonDisplayed()
            .assertContinueTrackingSectionDisplayed()

        graph.internetConnectionChecker.setConnected(false)

        showDetailsRobot.clickContinueTrackingMarkWatched(nextEpisodeTraktId)

        val postsWhileOffline = syncHistoryPostCount

        graph.internetConnectionChecker.setConnected(true)
        composeUi.waitForIdle()

        composeUi.waitUntil(timeoutMillis = 10_000) { syncHistoryPostCount > postsWhileOffline }
    }
}
