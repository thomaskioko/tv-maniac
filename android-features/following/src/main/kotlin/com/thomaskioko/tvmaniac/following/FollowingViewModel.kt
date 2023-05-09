package com.thomaskioko.tvmaniac.following

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomaskioko.tvmaniac.presentation.following.FollowingAction
import com.thomaskioko.tvmaniac.presentation.following.FollowingState
import com.thomaskioko.tvmaniac.presentation.following.FollowingStateMachine
import com.thomaskioko.tvmaniac.presentation.following.LoadingShows
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class FollowingViewModel(
    private val stateMachine: FollowingStateMachine,
) : ViewModel() {

    val state: MutableStateFlow<FollowingState> = MutableStateFlow(LoadingShows)

    init {
        viewModelScope.launch {
            stateMachine.state.collect {
                state.value = it
            }
        }
    }

    fun dispatch(action: FollowingAction) {
        viewModelScope.launch {
            stateMachine.dispatch(action)
        }
    }
}
