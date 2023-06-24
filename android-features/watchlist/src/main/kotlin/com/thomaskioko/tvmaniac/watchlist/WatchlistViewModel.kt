package com.thomaskioko.tvmaniac.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomaskioko.tvmaniac.presentation.watchlist.LoadingShows
import com.thomaskioko.tvmaniac.presentation.watchlist.WatchlistAction
import com.thomaskioko.tvmaniac.presentation.watchlist.WatchlistState
import com.thomaskioko.tvmaniac.presentation.watchlist.WatchlistStateMachine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class WatchlistViewModel(
    private val stateMachine: WatchlistStateMachine,
) : ViewModel() {

    val state: MutableStateFlow<WatchlistState> = MutableStateFlow(LoadingShows)

    init {
        viewModelScope.launch {
            stateMachine.state.collect {
                state.value = it
            }
        }
    }

    fun dispatch(action: WatchlistAction) {
        viewModelScope.launch {
            stateMachine.dispatch(action)
        }
    }
}
