package com.thomaskioko.tvmaniac.app.test

import androidx.compose.ui.test.AndroidComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runAndroidComposeUiTest
import androidx.datastore.preferences.core.edit
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.destroy
import com.arkivanov.essenty.lifecycle.resume
import com.thomaskioko.tvmaniac.app.di.ActivityGraph
import com.thomaskioko.tvmaniac.app.test.compose.TvManiacTestActivity
import com.thomaskioko.tvmaniac.app.test.compose.robot.CalendarRobot
import com.thomaskioko.tvmaniac.app.test.compose.robot.DiscoverRobot
import com.thomaskioko.tvmaniac.app.test.compose.robot.EpisodeSheetRobot
import com.thomaskioko.tvmaniac.app.test.compose.robot.HomeRobot
import com.thomaskioko.tvmaniac.app.test.compose.robot.LibraryRobot
import com.thomaskioko.tvmaniac.app.test.compose.robot.ProfileRobot
import com.thomaskioko.tvmaniac.app.test.compose.robot.ProgressRobot
import com.thomaskioko.tvmaniac.app.test.compose.robot.RootRobot
import com.thomaskioko.tvmaniac.app.test.compose.robot.SearchRobot
import com.thomaskioko.tvmaniac.app.test.compose.robot.SeasonDetailsRobot
import com.thomaskioko.tvmaniac.app.test.compose.robot.SettingsRobot
import com.thomaskioko.tvmaniac.app.test.compose.robot.ShowDetailsRobot
import com.thomaskioko.tvmaniac.app.test.compose.stubs.Scenarios
import com.thomaskioko.tvmaniac.testing.integration.MockEngineHandler
import com.thomaskioko.tvmaniac.util.testing.FlakyTestRule
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

/**
 * Base for app-level flow tests that drive the real `TvManiacTestActivity` through Compose.
 *
 * Subclasses wrap their assertions in [runAppFlowTest] which:
 *   * resets the singleton [MockEngineHandler] so stubs from the prior test do not leak,
 *   * recreates the [TestAppComponent] so `Dispatchers.Main.immediate` is captured AFTER
 *     `runAndroidComposeUiTest` installs its own [kotlinx.coroutines.test.TestDispatcher],
 *   * launches the activity inside the v2 `runTest` scope so Compose's main clock and the
 *     coroutines test scheduler share a single [kotlinx.coroutines.test.TestScheduler],
 *   * logs the fake Trakt repository out as a default starting state.
 *
 * The v2 [runAndroidComposeUiTest][androidx.compose.ui.test.v2.runAndroidComposeUiTest] uses
 * `StandardTestDispatcher` semantics: dispatched coroutines queue rather than running
 * immediately. Robots advance the scheduler through `waitForIdle` / `mainClock` calls inside
 * the existing assertion helpers.
 *
 * `@Config(application = TvManiacTestApplication::class)` is honoured by Robolectric;
 * instrumentation ignores it because `TvManiacInstrumentationRunner.newApplication` returns the
 * same Application subclass.
 */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [33], application = TvManiacTestApplication::class)
@OptIn(ExperimentalTestApi::class)
internal abstract class BaseAppFlowTest {

    @get:Rule
    val flakyRule = FlakyTestRule()

    private fun clearPersistedPreferencesViaCurrentGraph(application: TvManiacTestApplication) {
        runBlocking {
            application.graph.dataStore.edit { it.clear() }
        }
    }

    /**
     * Runs [block] inside `runAndroidComposeUiTest<TvManiacTestActivity>`, providing an
     * [AppFlowScope] with all robots, the dependency graph, and pre-seeded scenarios.
     *
     * The scope is rebuilt on every call. The application graph is reset before the activity
     * launches so the [kotlinx.coroutines.test.TestDispatcher] installed by
     * [runAndroidComposeUiTest] is the one captured by `AppCoroutineDispatchers`.
     */
    protected fun runAppFlowTest(block: AppFlowScope.() -> Unit) {
        MockEngineHandler.handler.reset()
        val application = InstrumentationRegistry.getInstrumentation()
            .targetContext
            .applicationContext as TvManiacTestApplication

        application.resetAppComponent()
        application.clearPersistentTestState()
        clearPersistedPreferencesViaCurrentGraph(application)

        runAndroidComposeUiTest<TvManiacTestActivity> {
            val graph = application.graph
            graph.traktAuthRepository.logout()
            val scope = AppFlowScope(this, graph)
            try {
                scope.block()
            } finally {
                scope.tearDown()
            }
        }
    }
}

/**
 * Per-test scope handed to [BaseAppFlowTest.runAppFlowTest]. Holds the running
 * [AndroidComposeUiTest], the freshly-built [TestAppComponent], the activity-scoped graph, and
 * cached robots and scenarios that share the same [composeUi].
 */
@OptIn(ExperimentalTestApi::class)
internal class AppFlowScope(
    val composeUi: AndroidComposeUiTest<TvManiacTestActivity>,
    val graph: TestAppComponent,
) {

    private val lifecycle: LifecycleRegistry = LifecycleRegistry()

    val activityGraph: ActivityGraph
        get() = checkNotNull(composeUi.activity).activityGraph

    /** Resumed Decompose [ComponentContext] for graph factories that need one outside of an activity. */
    val componentContext: ComponentContext by lazy {
        DefaultComponentContext(lifecycle = lifecycle).also { lifecycle.resume() }
    }

    val rootRobot: RootRobot by lazy { RootRobot(composeUi) }
    val homeRobot: HomeRobot by lazy { HomeRobot(composeUi) }
    val calendarRobot: CalendarRobot by lazy { CalendarRobot(composeUi) }
    val discoverRobot: DiscoverRobot by lazy { DiscoverRobot(composeUi) }
    val showDetailsRobot: ShowDetailsRobot by lazy { ShowDetailsRobot(composeUi) }
    val seasonDetailsRobot: SeasonDetailsRobot by lazy { SeasonDetailsRobot(composeUi) }
    val libraryRobot: LibraryRobot by lazy { LibraryRobot(composeUi) }
    val progressRobot: ProgressRobot by lazy { ProgressRobot(composeUi) }
    val profileRobot: ProfileRobot by lazy { ProfileRobot(composeUi) }
    val settingsRobot: SettingsRobot by lazy { SettingsRobot(composeUi) }
    val searchRobot: SearchRobot by lazy { SearchRobot(composeUi) }
    val episodeSheetRobot: EpisodeSheetRobot by lazy { EpisodeSheetRobot(composeUi) }

    val scenarios: Scenarios by lazy {
        Scenarios(
            mockHandler = MockEngineHandler.handler,
            graph = graph,
            rootRobot = rootRobot,
        )
    }

    internal fun tearDown() {
        lifecycle.destroy()
    }
}
