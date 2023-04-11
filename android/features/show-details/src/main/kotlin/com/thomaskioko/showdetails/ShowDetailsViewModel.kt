package com.thomaskioko.showdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomaskioko.tvmaniac.data.showdetails.LoadShowDetails
import com.thomaskioko.tvmaniac.data.showdetails.ShowDetailsAction
import com.thomaskioko.tvmaniac.data.showdetails.ShowDetailsState
import com.thomaskioko.tvmaniac.data.showdetails.ShowDetailsStateMachine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject


@Inject
class ShowDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val stateMachine: ShowDetailsStateMachine
) : ViewModel() {

    private val showId: Long = savedStateHandle["tvShowId"]!!

    val state: MutableStateFlow<ShowDetailsState> = MutableStateFlow(ShowDetailsState.Loading)

    init {

        viewModelScope.launch {
            stateMachine.state
                .collect {
                    state.value = it
                }
        }

        viewModelScope.launch {
            stateMachine.dispatch(LoadShowDetails(showId))
        }


    }

    fun dispatch(action : ShowDetailsAction) {
        viewModelScope.launch {
            stateMachine.dispatch(action)
        }
    }
}
