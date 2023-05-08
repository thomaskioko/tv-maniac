package com.thomaskioko.tvmaniac.shared.base.wrappers

import com.thomaskioko.tvmaniac.presentation.seasondetails.SeasonDetailsAction
import com.thomaskioko.tvmaniac.presentation.seasondetails.SeasonDetailsState
import com.thomaskioko.tvmaniac.presentation.seasondetails.SeasonDetailsStateMachine
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
    private val stateMachine:(Long) ->  SeasonDetailsStateMachine,
) {

    fun start(showId: Long, stateChangeListener: (SeasonDetailsState) -> Unit) {
        scope.main.launch {
            stateMachine(showId).state.collect {
                stateChangeListener(it)
            }
        }
    }

    fun dispatch(showId: Long, action: SeasonDetailsAction) {
        scope.main.launch {
            stateMachine(showId).dispatch(action)
        }
    }

    fun cancel() {
        scope.main.cancel()
    }
}
