package com.thomaskioko.tvmaniac.settings

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.settings.util.MainCoroutineRule
import com.thomaskioko.tvmaniac.shared.persistance.SettingsActions
import com.thomaskioko.tvmaniac.shared.persistance.SettingsContent
import com.thomaskioko.tvmaniac.shared.persistance.Theme
import com.thomaskioko.tvmaniac.shared.persistance.TvManiacPreferences
import com.thomaskioko.tvmaniac.trakt.api.ObserveTraktUserInteractor
import com.thomaskioko.tvmaniac.traktauth.ObserveTraktAuthStateInteractor
import com.thomaskioko.tvmaniac.traktauth.TraktAuthManager
import com.thomaskioko.tvmaniac.traktauth.TraktManager
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
internal class SettingsViewModelTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val themePreference: TvManiacPreferences = mockk()
    private val traktManager: TraktManager = mockk()
    private val traktAuthManager: TraktAuthManager = mockk()
    private val traktAuthInteractor: ObserveTraktAuthStateInteractor = mockk()
    private val observeTraktUserInteractor: ObserveTraktUserInteractor = mockk()
    private val testCoroutineDispatcher = StandardTestDispatcher()

    private val viewModel by lazy {
        SettingsViewModel(
            themePreference,
            traktManager,
            traktAuthManager,
            traktAuthInteractor,
            observeTraktUserInteractor,
            testCoroutineDispatcher
        )
    }

    @Test
    fun `givenThemeIsChanged verify updatedValueIsEmitted`() = runBlocking {
        every { themePreference.observeTheme() } returns flowOf(Theme.LIGHT)

        viewModel.observeState().test {
            viewModel.dispatch(SettingsActions.ThemeSelected("light"))
            awaitItem() shouldBe SettingsContent(
                theme = Theme.LIGHT,
                showPopup = false,
                loggedIn = false,
                showTraktDialog = false,
                traktFullName = "",
                traktUserPicUrl = "",
                traktUserName = ""
            )
            expectNoEvents()
        }
    }
}
