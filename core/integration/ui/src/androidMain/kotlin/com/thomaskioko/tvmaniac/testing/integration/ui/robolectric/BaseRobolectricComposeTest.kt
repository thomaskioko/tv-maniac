package com.thomaskioko.tvmaniac.testing.integration.ui.robolectric

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.destroy
import com.arkivanov.essenty.lifecycle.resume
import com.thomaskioko.tvmaniac.testing.integration.MockEngineResetRule
import com.thomaskioko.tvmaniac.testing.integration.ui.IntegrationTestEnvironment
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Base class for Robolectric-backed Compose integration tests.
 *
 * Provides [AndroidComposeTestRule], shared Decompose [ComponentContext], and
 * [MockEngineResetRule] to clear stubbed network state between tests.
 *
 * @param A Activity type hosting the Compose content.
 * @param G Dependency graph type.
 * @param activityClass Activity class passed to [createAndroidComposeRule].
 */
@RunWith(RobolectricTestRunner::class)
public abstract class BaseRobolectricComposeTest<A : ComponentActivity, G : Any>(
    activityClass: Class<A>,
) {

    /** Activity-scoped Compose rule that launches [A]. */
    @get:Rule
    public val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<A>, A> =
        createAndroidComposeRule(activityClass)

    /** Resets mock engine stubs between tests. */
    @get:Rule
    public val mockEngineResetRule: MockEngineResetRule = MockEngineResetRule()

    private val lifecycle: LifecycleRegistry = LifecycleRegistry()

    /** Resumed Decompose [ComponentContext] for components outside the activity. */
    protected val componentContext: ComponentContext by lazy {
        DefaultComponentContext(lifecycle = lifecycle).also { lifecycle.resume() }
    }

    /** Test environment exposing graph and network stubber. */
    protected abstract val environment: IntegrationTestEnvironment<G>

    /** Dependency graph the test can inspect or inject into. */
    protected val graph: G
        get() = environment.graph

    /** Invokes [onBeforeTest] to configure stubs before composition. */
    @Before
    public fun runOnBeforeTestHook() {
        onBeforeTest()
    }

    /** Destroys Decompose lifecycle to release references. */
    @After
    public fun destroyLifecycle() {
        lifecycle.destroy()
    }

    /**
     * Hook for subclasses to wire network stubs before activity composition.
     */
    protected open fun onBeforeTest() {}
}
