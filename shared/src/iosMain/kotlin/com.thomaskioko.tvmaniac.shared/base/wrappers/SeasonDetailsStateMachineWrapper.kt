package com.thomaskioko.tvmaniac.shared.base.wrappers

import com.thomaskioko.tvmaniac.domain.seasondetails.SeasonDetailsAction
import com.thomaskioko.tvmaniac.domain.seasondetails.SeasonDetailsState
import com.thomaskioko.tvmaniac.domain.seasondetails.SeasonDetailsStateMachine
import com.thomaskioko.tvmaniac.util.model.AppCoroutineScope
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
