package com.thomaskioko.tvmaniac.testing.integration.ui.robolectric

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.thomaskioko.tvmaniac.testing.integration.MockEngineHandler
import com.thomaskioko.tvmaniac.testing.integration.bindings.TestTmdbBindingContainer
import com.thomaskioko.tvmaniac.testing.integration.bindings.TestTraktBindingContainer
import com.thomaskioko.tvmaniac.testing.integration.ui.ClasspathFixtureReader
import com.thomaskioko.tvmaniac.testing.integration.ui.FixtureReader
import com.thomaskioko.tvmaniac.testing.integration.ui.IntegrationTestEnvironment
import com.thomaskioko.tvmaniac.testing.integration.ui.MockEngineNetworkStub
import com.thomaskioko.tvmaniac.testing.integration.ui.NetworkStub

/**
 * [IntegrationTestEnvironment] implementation for Robolectric tests.
 *
 * Resolves dependency graph from launched activity via [graphProvider]. Wires mock engines
 * and [ClasspathFixtureReader] into [MockEngineNetworkStub].
 *
 * @param A Activity type hosting the Compose content.
 * @param G Dependency graph type.
 * @property composeTestRule Compose rule driving the activity under test.
 * @param graphProvider Extracts graph from the launched activity instance.
 */
public open class RobolectricEnvironment<A : ComponentActivity, G : Any>(
    override val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<A>, A>,
    private val graphProvider: (A) -> G,
) : IntegrationTestEnvironment<G> {

    override val graph: G
        get() = graphProvider(composeTestRule.activity)

    /** Mock engine handler for TMDB Ktor client. */
    public val tmdbMock: MockEngineHandler = TestTmdbBindingContainer.handler

    /** Mock engine handler for Trakt Ktor client. */
    public val traktMock: MockEngineHandler = TestTraktBindingContainer.handler

    /** Reader used to resolve fixture-backed responses. */
    public val fixtureReader: FixtureReader = ClasspathFixtureReader()

    override val stubber: NetworkStub = MockEngineNetworkStub(
        traktMock = traktMock,
        tmdbMock = tmdbMock,
        fixtureReader = fixtureReader,
    )
}
