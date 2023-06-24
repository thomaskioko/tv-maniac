package com.thomaskioko.showdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomaskioko.tvmaniac.presentation.showdetails.ShowDetailsAction
import com.thomaskioko.tvmaniac.presentation.showdetails.ShowDetailsLoaded
import com.thomaskioko.tvmaniac.presentation.showdetails.ShowDetailsState
import com.thomaskioko.tvmaniac.presentation.showdetails.ShowDetailsStateMachine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class ShowDetailsViewModel(
    @Assisted savedStateHandle: SavedStateHandle,
    stateMachine: (Long) -> ShowDetailsStateMachine,
) : ViewModel() {

    private val showId: Long = savedStateHandle["tvShowId"]!!
    private val detailStateMachine = stateMachine(showId)

    val state: MutableStateFlow<ShowDetailsState> = MutableStateFlow(ShowDetailsLoaded.EMPTY_DETAIL_STATE)

    init {

        viewModelScope.launch {
            detailStateMachine.state
                .collect {
                    state.value = it
                }
        }
    }

    fun dispatch(action: ShowDetailsAction) {
        viewModelScope.launch {
            detailStateMachine.dispatch(action)
        }
    }
}
