package com.thomaskioko.tvmaniac.shared.base.wrappers

import com.thomaskioko.tvmaniac.domain.trailers.TrailersAction
import com.thomaskioko.tvmaniac.domain.trailers.TrailersState
import com.thomaskioko.tvmaniac.domain.trailers.TrailersStateMachine
import com.thomaskioko.tvmaniac.util.model.AppCoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

/**
 * A wrapper class around [TrailersStateMachine] handling `Flow` and suspend functions on iOS.
 */
@Inject
class TrailersStateMachineWrapper(
    private val scope: AppCoroutineScope,
    private val stateMachine: TrailersStateMachine,
) {

    fun start(stateChangeListener: (TrailersState) -> Unit) {
        scope.main.launch {
            stateMachine.state.collect {
                stateChangeListener(it)
            }
        }
    }

    fun dispatch(action: TrailersAction) {
        scope.main.launch {
            stateMachine.dispatch(action)
        }
    }

    fun cancel() {
        scope.main.cancel()
    }
}