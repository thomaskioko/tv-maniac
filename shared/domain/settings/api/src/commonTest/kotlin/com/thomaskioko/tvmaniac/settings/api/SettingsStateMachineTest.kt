package com.thomaskioko.tvmaniac.settings.api

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.test.runBlockingTest
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class SettingsStateMachineTest {

    private val settingsRepository = FakeSettingsRepository()
    private val stateMachine = SettingsStateMachine(settingsRepository)

    @Test
    fun initial_state_emits_expected_result() = runBlockingTest {
        stateMachine.state.test {
            awaitItem() shouldBe SettingsContent.EMPTY // Initial State
        }
    }

    @Test
    fun when_theme_is_updated_expected_result_is_emitted() = runBlockingTest {
        stateMachine.state.test {

            awaitItem() shouldBe SettingsContent.EMPTY // Initial State

            stateMachine.dispatch(ChangeThemeClicked)
            awaitItem() shouldBe SettingsContent.EMPTY.copy( //  Popup is shown
                showPopup = true
            )

            settingsRepository.setTheme(Theme.DARK)
            stateMachine.dispatch(ThemeSelected(Theme.DARK))

            awaitItem() shouldBe SettingsContent.EMPTY.copy( // Theme is updated
                showPopup = true,
                theme = Theme.DARK
            )
            awaitItem() shouldBe SettingsContent.EMPTY.copy( // Popup is dismissed.
                showPopup = false,
                theme = Theme.DARK
            )
        }
    }

    @Test
    fun when_dialog_is_dismissed_expected_result_is_emitted() = runBlockingTest {
        stateMachine.state.test {

            stateMachine.dispatch(ChangeThemeClicked)
            stateMachine.dispatch(DimissThemeClicked)

            awaitItem() shouldBe SettingsContent.EMPTY // Initial State
            awaitItem() shouldBe SettingsContent.EMPTY.copy( // Popup is shown
                showPopup = true,
            )
            awaitItem() shouldBe SettingsContent.EMPTY.copy( // Popup is dismissed.
                showPopup = false,
            )
        }
    }
}