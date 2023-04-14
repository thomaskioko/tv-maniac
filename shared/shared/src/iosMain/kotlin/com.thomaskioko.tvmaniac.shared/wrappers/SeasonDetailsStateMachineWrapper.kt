package com.thomaskioko.tvmaniac.shared.wrappers

import com.thomaskioko.tvmaniac.base.model.AppCoroutineScope
import com.thomaskioko.tvmaniac.data.seasondetails.SeasonDetailsAction
import com.thomaskioko.tvmaniac.data.seasondetails.SeasonDetailsState
import com.thomaskioko.tvmaniac.data.seasondetails.SeasonDetailsStateMachine
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

/**
 * A wrapper class around [SeasonDetailsStateMachine] handling `Flow` and suspend functions on iOS.
 */
@Inject
class SeasonDetailsStateMachineWrapper(
    private val scope: AppCoroutineScope,
    private val stateMachine: SeasonDetailsStateMachine,
) {

    fun start(stateChangeListener: (SeasonDetailsState) -> Unit) {
        scope.main.launch {
            stateMachine.state.collect {
                stateChangeListener(it)
            }
        }
    }

    fun dispatch(action: SeasonDetailsAction) {
        scope.main.launch {
            stateMachine.dispatch(action)
        }
    }

    fun cancel() {
        scope.main.cancel()
    }
}
