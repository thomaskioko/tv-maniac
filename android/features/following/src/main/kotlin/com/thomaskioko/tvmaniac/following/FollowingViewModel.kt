package com.thomaskioko.tvmaniac.following

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomaskioko.tvmaniac.core.util.CoroutineScopeOwner
import com.thomaskioko.tvmaniac.shared.core.ui.Store
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowingViewModel @Inject constructor(
    private val interactor: ObserveFollowingInteractor,
) : Store<FollowingState, FollowingAction, FollowingEffect>,
    CoroutineScopeOwner, ViewModel() {

    override val coroutineScope: CoroutineScope
        get() = viewModelScope

    override val state: MutableStateFlow<FollowingState> = MutableStateFlow(FollowingState.Empty)

    private val sideEffect = MutableSharedFlow<FollowingEffect>()

    init {
        dispatch(FollowingAction.LoadFollowedShows)
    }

    override fun observeState(): StateFlow<FollowingState> = state

    override fun observeSideEffect(): Flow<FollowingEffect> = sideEffect

    override fun dispatch(action: FollowingAction) {
        val oldState = state.value

        when (action) {
            is FollowingAction.Error -> {
                coroutineScope.launch {
                    sideEffect.emit(FollowingEffect.Error(action.message))
                }
            }
            FollowingAction.LoadFollowedShows -> {
                with(state) {
                    interactor.execute(Unit) {
                        onStart {
                            coroutineScope.launch { emit(oldState.copy(isLoading = true)) }
                        }

                        onNext {
                            coroutineScope.launch {
                                emit(
                                    oldState.copy(
                                        isLoading = false,
                                        list = it
                                    )
                                )
                            }
                        }

                        onError {
                            coroutineScope.launch { emit(oldState.copy(isLoading = false)) }
                            dispatch(FollowingAction.Error(it.message ?: "Something went wrong"))
                        }
                    }
                }
            }
        }
    }
}
