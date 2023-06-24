package com.thomaskioko.tvmaniac.shared.base.wrappers

import com.thomaskioko.tvmaniac.presentation.watchlist.WatchlistAction
import com.thomaskioko.tvmaniac.presentation.watchlist.WatchlistState
import com.thomaskioko.tvmaniac.presentation.watchlist.WatchlistStateMachine
import com.thomaskioko.tvmaniac.util.model.AppCoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

/**
 * A wrapper class around [WatchlistStateMachine] handling `Flow` and suspend functions on iOS.
 */
@Inject
class WatchlistStateMachineWrapper(
    private val scope: AppCoroutineScope,
    private val stateMachine: WatchlistStateMachine,
) {

    fun start(stateChangeListener: (WatchlistState) -> Unit) {
        scope.main.launch {
            stateMachine.state.collect {
                stateChangeListener(it)
            }
        }
    }

    fun dispatch(action: WatchlistAction) {
        scope.main.launch {
            stateMachine.dispatch(action)
        }
    }

    fun cancel() {
        scope.main.cancel()
    }
}
