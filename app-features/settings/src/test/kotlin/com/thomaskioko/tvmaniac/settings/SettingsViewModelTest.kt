package com.thomaskioko.tvmaniac.settings

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.settings.api.TvManiacPreferences
import com.thomaskioko.tvmaniac.settings.api.TvManiacPreferences.Theme
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Test

internal class SettingsViewModelTest {

    private val themePreference: TvManiacPreferences = mockk()
    private val testCoroutineDispatcher = TestCoroutineDispatcher()

    private val viewModel by lazy {
        SettingsViewModel(
            themePreference,
            testCoroutineDispatcher
        )
    }

    @Test
    fun `givenThemeIsChanged verify updatedValueIsEmitted`() = runBlocking {
        every { themePreference.observeTheme() } returns flowOf(Theme.LIGHT)

        viewModel.themeState.test {

            viewModel.updateTheme("light")

            awaitItem() shouldBe Theme.LIGHT
            awaitComplete()
        }
    }
}
