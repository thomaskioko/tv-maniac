package com.thomaskioko.tvmaniac.shared.wrappers

import com.thomaskioko.tvmaniac.util.model.AppCoroutineScope
import com.thomaskioko.tvmaniac.shared.domain.discover.DiscoverStateMachine
import com.thomaskioko.tvmaniac.shared.domain.discover.ShowsAction
import com.thomaskioko.tvmaniac.shared.domain.discover.ShowsState
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

/**
 * A wrapper class around [DiscoverStateMachine] handling `Flow` and suspend functions on iOS.
 */
@Inject
class DiscoverStateMachineWrapper(
    private val scope: AppCoroutineScope,
    private val stateMachine: DiscoverStateMachine,
) {

    fun start(stateChangeListener: (ShowsState) -> Unit) {
        scope.main.launch {
            stateMachine.state.collect {
                stateChangeListener(it)
            }
        }
    }

    fun dispatch(action: ShowsAction) {
        scope.main.launch {
            stateMachine.dispatch(action)
        }
    }

    fun cancel() {
        scope.main.cancel()
    }

}