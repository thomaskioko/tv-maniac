package com.thomaskioko.tvmaniac.shared.base.wrappers

import com.thomaskioko.tvmaniac.presentation.watchlist.LibraryState
import com.thomaskioko.tvmaniac.presentation.watchlist.LibraryStateMachine
import com.thomaskioko.tvmaniac.presentation.watchlist.WatchlistAction
import com.thomaskioko.tvmaniac.util.model.AppCoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

/**
 * A wrapper class around [LibraryStateMachine] handling `Flow` and suspend functions on iOS.
 */
@Inject
class WatchlistStateMachineWrapper(
    private val scope: AppCoroutineScope,
    private val stateMachine: LibraryStateMachine,
) {

    fun start(stateChangeListener: (LibraryState) -> Unit) {
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
