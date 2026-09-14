package com.thomaskioko.tvmaniac.app.test.compose.flows.settings

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import com.thomaskioko.tvmaniac.app.test.compose.stubs.TEST_SIMKL_ACCOUNT_ID
import com.thomaskioko.tvmaniac.testing.integration.Endpoints
import com.thomaskioko.tvmaniac.testing.integration.MockEngineHandler
import com.thomaskioko.tvmaniac.testing.integration.TEST_PROFILE_SLUG
import com.thomaskioko.tvmaniac.testtags.home.HomeTestTags
import com.thomaskioko.tvmaniac.testtags.settings.SettingsTestTags
import io.ktor.http.HttpMethod
import kotlinx.coroutines.awaitCancellation
import org.junit.Test

internal class SettingsOfflineFlowTest : BaseAppFlowTest() {

    @Test
    fun givenTraktSessionOffline_whenSettingsOpened_thenSettingsContentIsShown() = runAppFlowTest {
        scenarios.stubPublicCatalog()
        scenarios.stubActiveProvider(SyncProviderSource.TRAKT)

        homeRobot
            .clickProfileTab()
            .assertTabSelected(HomeTestTags.PROFILE_TAB)
        profileRobot.assertUserCardDisplayed(slug = TEST_PROFILE_SLUG)

        graph.internetConnectionChecker.setConnected(false)

        profileRobot.clickSettingsButton()

        settingsRobot
            .assertSettingsScreenDisplayed()
            .assertDisplayed(SettingsTestTags.GENERAL_LAYOUT_ROW_TEST_TAG)
    }

    @Test
    fun givenSimklSessionOffline_whenSettingsOpened_thenSettingsContentIsShown() = runAppFlowTest {
        scenarios.stubPublicCatalog()
        scenarios.stubActiveProvider(SyncProviderSource.SIMKL)

        homeRobot
            .clickProfileTab()
            .assertTabSelected(HomeTestTags.PROFILE_TAB)
        profileRobot.assertUserCardDisplayed(slug = TEST_SIMKL_ACCOUNT_ID.toString())

        MockEngineHandler.handler.stub(
            method = HttpMethod.Post,
            path = Endpoints.Simkl.UsersSettings.path,
            host = Endpoints.Simkl.UsersSettings.host,
        ) { _ -> awaitCancellation() }
        graph.internetConnectionChecker.setConnected(false)

        profileRobot.clickSettingsButton()

        settingsRobot
            .assertSettingsScreenDisplayed()
            .assertDisplayed(SettingsTestTags.GENERAL_LAYOUT_ROW_TEST_TAG)
    }
}
