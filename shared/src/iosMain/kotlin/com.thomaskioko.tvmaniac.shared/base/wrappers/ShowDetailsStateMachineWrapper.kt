package com.thomaskioko.tvmaniac.shared.base.wrappers

import com.thomaskioko.tvmaniac.presentation.showdetails.ShowDetailsAction
import com.thomaskioko.tvmaniac.presentation.showdetails.ShowDetailsState
import com.thomaskioko.tvmaniac.presentation.showdetails.ShowDetailsStateMachine
import com.thomaskioko.tvmaniac.util.model.AppCoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

/**
 * A wrapper class around [ShowDetailsStateMachineWrapper] handling `Flow` and suspend functions on iOS.
 */
@Inject
class ShowDetailsStateMachineWrapper(
    private val scope: AppCoroutineScope,
    private val stateMachine: (Long) -> ShowDetailsStateMachine,
) {

    fun start(showId: Long, stateChangeListener: (ShowDetailsState) -> Unit) {
        scope.main.launch {
            stateMachine(showId).state.collect {
                stateChangeListener(it)
            }
        }
    }

    fun dispatch(showId: Long, action: ShowDetailsAction) {
        scope.main.launch {
            stateMachine(showId).dispatch(action)
        }
    }

    fun cancel() {
        scope.main.cancel()
    }
}
