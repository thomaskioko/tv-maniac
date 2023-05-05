package com.thomaskioko.tvmaniac.shared.base.wrappers

import com.thomaskioko.tvmaniac.domain.following.FollowingAction
import com.thomaskioko.tvmaniac.domain.following.FollowingState
import com.thomaskioko.tvmaniac.domain.following.FollowingStateMachine
import com.thomaskioko.tvmaniac.util.model.AppCoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

/**
 * A wrapper class around [FollowingStateMachine] handling `Flow` and suspend functions on iOS.
 */
@Inject
class FollowingStateMachineWrapper(
    private val scope: AppCoroutineScope,
    private val stateMachine: FollowingStateMachine,
) {

    fun start(stateChangeListener: (FollowingState) -> Unit) {
        scope.main.launch {
            stateMachine.state.collect {
                stateChangeListener(it)
            }
        }
    }

    fun dispatch(action: FollowingAction) {
        scope.main.launch {
            stateMachine.dispatch(action)
        }
    }

    fun cancel() {
        scope.main.cancel()
    }
}
