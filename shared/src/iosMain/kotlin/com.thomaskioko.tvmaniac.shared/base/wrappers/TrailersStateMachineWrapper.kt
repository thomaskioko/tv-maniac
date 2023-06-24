package com.thomaskioko.tvmaniac.shared.base.wrappers

import com.thomaskioko.tvmaniac.presentation.trailers.TrailersAction
import com.thomaskioko.tvmaniac.presentation.trailers.TrailersState
import com.thomaskioko.tvmaniac.presentation.trailers.TrailersStateMachine
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
    private val stateMachine: (Long) -> TrailersStateMachine,
) {

    fun start(showId: Long, stateChangeListener: (TrailersState) -> Unit) {
        scope.main.launch {
            stateMachine(showId).state.collect {
                stateChangeListener(it)
            }
        }
    }

    fun dispatch(showId: Long, action: TrailersAction) {
        scope.main.launch {
            stateMachine(showId).dispatch(action)
        }
    }

    fun cancel() {
        scope.main.cancel()
    }
}
