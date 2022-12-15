package com.thomaskioko.tvmaniac.seasondetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomaskioko.tvmaniac.seasondetails.api.LoadSeasonDetails
import com.thomaskioko.tvmaniac.seasondetails.api.Loading
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsAction
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsState
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsStateMachine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeasonDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val stateMachine: SeasonDetailsStateMachine
) : ViewModel() {

    private val showId: Int = savedStateHandle["showId"]!!

    val state: MutableStateFlow<SeasonDetailsState> = MutableStateFlow(Loading)

    init {

        viewModelScope.launch {
            stateMachine.state
                .collect {
                    state.value = it
                }
        }

        viewModelScope.launch {
            stateMachine.dispatch(LoadSeasonDetails(showId))
        }
    }

    fun dispatch(action : SeasonDetailsAction) {
        viewModelScope.launch {
            stateMachine.dispatch(action)
        }
    }

}
