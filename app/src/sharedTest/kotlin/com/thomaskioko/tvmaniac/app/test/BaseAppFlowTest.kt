package com.thomaskioko.tvmaniac.app.test

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
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseInstrumentationComposeTest
import kotlinx.coroutines.runBlocking
import org.robolectric.annotation.Config

/**
 * Runner-agnostic base for app-level flow tests.
 *
 * AndroidX test runner delegates to `RobolectricTestRunner` when Robolectric is on the JVM
 * classpath (the Robolectric variant of `app/src/test/`) and runs natively on emulators when it is
 * not (the `app/src/androidTest/` variant). `@Config` annotation specifies which Application
 * subclass to instantiate; instrumentation ignores it because `TvManiacInstrumentationRunner` already
 * supplies `TvManiacTestApplication` via `newApplication()`.
 */
@Config(sdk = [33], application = TvManiacTestApplication::class)
internal abstract class BaseAppFlowTest : BaseInstrumentationComposeTest<TvManiacTestActivity, TestAppComponent>(
    activityClass = TvManiacTestActivity::class.java,
) {

    override val graph: TestAppComponent
        get() = (composeTestRule.activity.application as TvManiacTestApplication).graph

    protected val activityGraph: ActivityGraph
        get() = composeTestRule.activity.activityGraph

    protected val scenarios: Scenarios by lazy {
        Scenarios(
            mockHandler = MockEngineHandler.handler,
            graph = graph,
            rootRobot = rootRobot,
        )
    }

    protected val rootRobot: RootRobot by lazy { RootRobot(composeTestRule) }
    protected val homeRobot: HomeRobot by lazy { HomeRobot(composeTestRule) }
    protected val calendarRobot: CalendarRobot by lazy { CalendarRobot(composeTestRule) }
    protected val discoverRobot: DiscoverRobot by lazy { DiscoverRobot(composeTestRule) }
    protected val showDetailsRobot: ShowDetailsRobot by lazy { ShowDetailsRobot(composeTestRule) }
    protected val seasonDetailsRobot: SeasonDetailsRobot by lazy {
        SeasonDetailsRobot(
            composeTestRule,
        )
    }
    protected val libraryRobot: LibraryRobot by lazy { LibraryRobot(composeTestRule) }
    protected val progressRobot: ProgressRobot by lazy { ProgressRobot(composeTestRule) }
    protected val profileRobot: ProfileRobot by lazy { ProfileRobot(composeTestRule) }
    protected val settingsRobot: SettingsRobot by lazy { SettingsRobot(composeTestRule) }
    protected val searchRobot: SearchRobot by lazy { SearchRobot(composeTestRule) }
    protected val episodeSheetRobot: EpisodeSheetRobot by lazy { EpisodeSheetRobot(composeTestRule) }

    override fun onBeforeTest() {
        runBlocking { graph.traktAuthRepository.logout() }
    }
}
