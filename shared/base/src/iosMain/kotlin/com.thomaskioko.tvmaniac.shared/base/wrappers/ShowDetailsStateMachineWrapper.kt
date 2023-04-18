package com.thomaskioko.tvmaniac.shared.base.wrappers

import com.thomaskioko.tvmaniac.util.model.AppCoroutineScope
import com.thomaskioko.tvmaniac.domain.showdetails.ShowDetailsAction
import com.thomaskioko.tvmaniac.domain.showdetails.ShowDetailsState
import com.thomaskioko.tvmaniac.domain.showdetails.ShowDetailsStateMachine
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

/**
 * A wrapper class around [ShowDetailsStateMachineWrapper] handling `Flow` and suspend functions on iOS.
 */
@Inject
class ShowDetailsStateMachineWrapper(
    private val scope: AppCoroutineScope,
    private val stateMachine: ShowDetailsStateMachine,
) {

    fun start(stateChangeListener: (ShowDetailsState) -> Unit) {
        scope.main.launch {
            stateMachine.state.collect {
                stateChangeListener(it)
            }
        }
    }

    fun dispatch(action: ShowDetailsAction) {
        scope.main.launch {
            stateMachine.dispatch(action)
        }
    }

    fun cancel() {
        scope.main.cancel()
    }
}