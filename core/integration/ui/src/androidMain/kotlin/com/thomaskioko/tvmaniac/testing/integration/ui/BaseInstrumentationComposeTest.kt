package com.thomaskioko.tvmaniac.testing.integration.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.destroy
import com.arkivanov.essenty.lifecycle.resume
import com.thomaskioko.tvmaniac.testing.integration.MockEngineResetRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

/**
 * Base class for Compose integration tests on Android.
 *
 * Runs under `@RunWith(AndroidJUnit4::class)`, which delegates to `RobolectricTestRunner` when
 * Robolectric is on the JVM classpath (`app/src/test/`) and uses the native runner on emulators or
 * devices (`app/src/androidTest/`). The same subclass therefore exercises both runners without
 * duplication.
 *
 * Provides [androidx.compose.ui.test.junit4.AndroidComposeTestRule], shared Decompose [com.arkivanov.decompose.ComponentContext], and [com.thomaskioko.tvmaniac.testing.integration.MockEngineResetRule]
 * to clear stubbed network state between tests. The Decompose [com.arkivanov.essenty.lifecycle.LifecycleRegistry] is synthetic and
 * not bound to the activity's real Android lifecycle — acceptable for foreground-only flows.
 *
 * @param A Activity type hosting the Compose content.
 * @param G Dependency graph type.
 * @param activityClass Activity class passed to [androidx.compose.ui.test.junit4.v2.createAndroidComposeRule].
 */
@RunWith(AndroidJUnit4::class)
public abstract class BaseInstrumentationComposeTest<A : ComponentActivity, G : Any>(
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

    /** Resumed Decompose [com.arkivanov.decompose.ComponentContext] for components outside the activity. */
    protected val componentContext: ComponentContext by lazy {
        DefaultComponentContext(lifecycle = lifecycle).also { lifecycle.resume() }
    }

    /** Dependency graph the test can inspect or inject into. */
    protected abstract val graph: G

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
