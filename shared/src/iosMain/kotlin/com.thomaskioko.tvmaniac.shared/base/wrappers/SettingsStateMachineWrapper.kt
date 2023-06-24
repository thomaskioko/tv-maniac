package com.thomaskioko.tvmaniac.shared.base.wrappers

import com.thomaskioko.tvmaniac.presentation.settings.SettingsActions
import com.thomaskioko.tvmaniac.presentation.settings.SettingsState
import com.thomaskioko.tvmaniac.presentation.settings.SettingsStateMachine
import com.thomaskioko.tvmaniac.util.model.AppCoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

/**
 * A wrapper class around [SettingsStateMachine] handling `Flow` and suspend functions on iOS.
 */
@Inject
class SettingsStateMachineWrapper(
    private val scope: AppCoroutineScope,
    private val stateMachine: SettingsStateMachine,
) {

    fun start(stateChangeListener: (SettingsState) -> Unit) {
        scope.main.launch {
            stateMachine.state.collect {
                stateChangeListener(it)
            }
        }
    }

    fun dispatch(action: SettingsActions) {
        scope.main.launch {
            stateMachine.dispatch(action)
        }
    }

    fun cancel() {
        scope.main.cancel()
    }
}
