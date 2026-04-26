package com.thomaskioko.tvmaniac.app.test.util

import com.thomaskioko.tvmaniac.app.di.ActivityGraph
import com.thomaskioko.tvmaniac.app.test.TestAppComponent
import com.thomaskioko.tvmaniac.app.test.TvManiacTestApplication
import com.thomaskioko.tvmaniac.app.test.compose.TvManiacTestActivity
import com.thomaskioko.tvmaniac.app.test.compose.robot.CalendarRobot
import com.thomaskioko.tvmaniac.app.test.compose.robot.DiscoverRobot
import com.thomaskioko.tvmaniac.app.test.compose.robot.EpisodeSheetRobot
import com.thomaskioko.tvmaniac.app.test.compose.robot.HomeRobot
import com.thomaskioko.tvmaniac.app.test.compose.robot.LibraryRobot
import com.thomaskioko.tvmaniac.app.test.compose.robot.ProfileRobot
import com.thomaskioko.tvmaniac.app.test.compose.robot.ProgressRobot
import com.thomaskioko.tvmaniac.app.test.compose.robot.SearchRobot
import com.thomaskioko.tvmaniac.app.test.compose.robot.SeasonDetailsRobot
import com.thomaskioko.tvmaniac.app.test.compose.robot.SettingsRobot
import com.thomaskioko.tvmaniac.app.test.compose.robot.ShowDetailsRobot
import com.thomaskioko.tvmaniac.app.test.compose.stubs.Scenarios
import com.thomaskioko.tvmaniac.testing.integration.ui.robolectric.BaseRobolectricComposeTest
import com.thomaskioko.tvmaniac.testing.integration.ui.robolectric.RobolectricEnvironment
import kotlinx.coroutines.runBlocking
import org.robolectric.annotation.Config

@Config(sdk = [33], application = TvManiacTestApplication::class)
internal abstract class BaseAppRobolectricTest : BaseRobolectricComposeTest<TvManiacTestActivity, TestAppComponent>(
    activityClass = TvManiacTestActivity::class.java,
) {

    override val environment: RobolectricEnvironment<TvManiacTestActivity, TestAppComponent> by lazy {
        RobolectricEnvironment(
            composeTestRule = composeTestRule,
            graphProvider = { (it.application as TvManiacTestApplication).graph },
        )
    }

    protected val activityGraph: ActivityGraph
        get() = composeTestRule.activity.activityGraph

    protected val scenarios: Scenarios by lazy { Scenarios(stubber = environment.stubber, graph = graph) }

    protected val homeRobot: HomeRobot by lazy { HomeRobot(composeTestRule) }
    protected val calendarRobot: CalendarRobot by lazy { CalendarRobot(composeTestRule) }
    protected val discoverRobot: DiscoverRobot by lazy { DiscoverRobot(composeTestRule) }
    protected val showDetailsRobot: ShowDetailsRobot by lazy { ShowDetailsRobot(composeTestRule) }
    protected val seasonDetailsRobot: SeasonDetailsRobot by lazy { SeasonDetailsRobot(composeTestRule) }
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
