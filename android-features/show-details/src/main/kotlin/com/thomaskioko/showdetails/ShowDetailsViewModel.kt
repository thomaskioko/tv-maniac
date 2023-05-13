package com.thomaskioko.showdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomaskioko.tvmaniac.presentation.showdetails.ShowDetailsAction
import com.thomaskioko.tvmaniac.presentation.showdetails.ShowDetailsState
import com.thomaskioko.tvmaniac.presentation.showdetails.ShowDetailsStateMachine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class ShowDetailsViewModel(
    @Assisted savedStateHandle: SavedStateHandle,
    private val stateMachine: (Long) -> ShowDetailsStateMachine,
) : ViewModel() {

    private val showId: Long = savedStateHandle["tvShowId"]!!

    val state: MutableStateFlow<ShowDetailsState> = MutableStateFlow(ShowDetailsState.Loading)

    init {

        viewModelScope.launch {
            stateMachine(showId).state
                .collect {
                    state.value = it
                }
        }
    }

    fun dispatch(action: ShowDetailsAction) {
        viewModelScope.launch {
            stateMachine(showId).dispatch(action)
        }
    }
}
