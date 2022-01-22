package com.thomaskioko.tvmaniac.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomaskioko.tvmaniac.interactors.ObserveFollowingInteractor
import com.thomaskioko.tvmaniac.interactors.WatchlistAction
import com.thomaskioko.tvmaniac.interactors.WatchlistEffect
import com.thomaskioko.tvmaniac.interactors.WatchlistState
import com.thomaskioko.tvmaniac.shared.core.CoroutineScopeOwner
import com.thomaskioko.tvmaniac.shared.core.store.Store
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    private val interactor: ObserveFollowingInteractor,
) : Store<WatchlistState, WatchlistAction, WatchlistEffect>,
    CoroutineScopeOwner, ViewModel() {

    override val coroutineScope: CoroutineScope
        get() = viewModelScope

    private val state = MutableStateFlow(WatchlistState.Empty)
    private val sideEffect = MutableSharedFlow<WatchlistEffect>()

    init {
        dispatch(WatchlistAction.LoadWatchlist)
    }

    override fun observeState(): StateFlow<WatchlistState> = state

    override fun observeSideEffect(): Flow<WatchlistEffect> = sideEffect

    override fun dispatch(action: WatchlistAction) {
        val oldState = state.value

        when (action) {
            is WatchlistAction.Error -> {
                coroutineScope.launch {
                    sideEffect.emit(WatchlistEffect.Error(action.message))
                }
            }
            WatchlistAction.LoadWatchlist -> {
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
                            dispatch(WatchlistAction.Error(it.message ?: "Something went wrong"))
                        }
                    }
                }
            }
        }
    }
}
