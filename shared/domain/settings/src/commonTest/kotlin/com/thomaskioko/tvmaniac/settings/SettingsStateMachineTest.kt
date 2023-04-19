package com.thomaskioko.tvmaniac.settings

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.datastore.api.Theme
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsStateMachineTest {

    private val settingsRepository = FakeDatastoreRepository()
    private val stateMachine = SettingsStateMachine(settingsRepository)

    @Test
    fun initial_state_emits_expected_result() = runTest {
        stateMachine.state.test {
            awaitItem() shouldBe SettingsContent.EMPTY // Initial State
        }
    }

    @Test
    fun when_theme_is_updated_expected_result_is_emitted() = runTest {
        stateMachine.state.test {
            awaitItem() shouldBe SettingsContent.EMPTY // Initial State

            stateMachine.dispatch(ChangeThemeClicked)
            awaitItem() shouldBe SettingsContent.EMPTY.copy( //  Popup is shown
                showPopup = true,
            )

            settingsRepository.setTheme(Theme.DARK)
            stateMachine.dispatch(ThemeSelected(Theme.DARK))

            awaitItem() shouldBe SettingsContent.EMPTY.copy( // Theme is updated
                showPopup = true,
                theme = Theme.DARK,
            )
            awaitItem() shouldBe SettingsContent.EMPTY.copy( // Popup is dismissed.
                showPopup = false,
                theme = Theme.DARK,
            )
        }
    }

    @Test
    fun when_dialog_is_dismissed_expected_result_is_emitted() = runTest {
        stateMachine.state.test {
            awaitItem() shouldBe SettingsContent.EMPTY // Initial State

            stateMachine.dispatch(ChangeThemeClicked)

            awaitItem() shouldBe SettingsContent.EMPTY.copy( // Popup is shown
                showPopup = true,
            )

            stateMachine.dispatch(DimissThemeClicked)

            awaitItem() shouldBe SettingsContent.EMPTY.copy( // Popup is dismissed.
                showPopup = false,
            )
        }
    }
}
