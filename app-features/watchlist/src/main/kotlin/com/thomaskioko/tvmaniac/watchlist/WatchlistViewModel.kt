package com.thomaskioko.tvmaniac.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomaskioko.tvmaniac.core.Store
import com.thomaskioko.tvmaniac.interactor.GetWatchListInteractor
import com.thomaskioko.tvmaniac.presentation.contract.WatchlistAction
import com.thomaskioko.tvmaniac.presentation.contract.WatchlistEffect
import com.thomaskioko.tvmaniac.presentation.contract.WatchlistState
import com.thomaskioko.tvmaniac.presentation.model.TvShow
import com.thomaskioko.tvmaniac.util.DomainResultState
import com.thomaskioko.tvmaniac.util.invoke
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    private val interactor: GetWatchListInteractor,
) : Store<WatchlistState, WatchlistAction, WatchlistEffect>, ViewModel() {

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
                viewModelScope.launch {
                    sideEffect.emit(WatchlistEffect.Error(action.message))
                }
            }
            WatchlistAction.LoadWatchlist -> {
                viewModelScope.launch {
                    interactor.invoke()
                        .collect {
                            state.emit(it.stateReducer(oldState))
                        }
                }
            }
        }
    }

    private fun DomainResultState<List<TvShow>>.stateReducer(state: WatchlistState): WatchlistState {
        return when (this) {
            is DomainResultState.Error -> {
                dispatch(WatchlistAction.Error(message))
                state.copy(isLoading = false)
            }
            is DomainResultState.Loading -> state.copy(isLoading = true)
            is DomainResultState.Success -> state.copy(isLoading = false, list = data)
        }
    }
}
