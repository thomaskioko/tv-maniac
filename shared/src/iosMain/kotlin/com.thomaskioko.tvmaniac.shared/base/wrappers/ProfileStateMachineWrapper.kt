package com.thomaskioko.tvmaniac.shared.base.wrappers

import com.thomaskioko.tvmaniac.presentation.profile.ProfileActions
import com.thomaskioko.tvmaniac.presentation.profile.ProfileState
import com.thomaskioko.tvmaniac.presentation.profile.ProfileStateMachine
import com.thomaskioko.tvmaniac.util.model.AppCoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

/**
 * A wrapper class around [ProfileStateMachineWrapper] handling `Flow` and suspend functions on iOS.
 */
@Inject
class ProfileStateMachineWrapper(
    private val scope: AppCoroutineScope,
    private val stateMachine: ProfileStateMachine,
) {

    fun start(stateChangeListener: (ProfileState) -> Unit) {
        scope.main.launch {
            stateMachine.state.collect {
                stateChangeListener(it)
            }
        }
    }

    fun dispatch(action: ProfileActions) {
        scope.main.launch {
            stateMachine.dispatch(action)
        }
    }

    fun cancel() {
        scope.main.cancel()
    }
}
