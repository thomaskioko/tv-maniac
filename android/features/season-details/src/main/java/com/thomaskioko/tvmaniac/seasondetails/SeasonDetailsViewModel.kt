package com.thomaskioko.tvmaniac.seasondetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomaskioko.tvmaniac.data.seasondetails.LoadSeasonDetails
import com.thomaskioko.tvmaniac.data.seasondetails.Loading
import com.thomaskioko.tvmaniac.data.seasondetails.SeasonDetailsAction
import com.thomaskioko.tvmaniac.data.seasondetails.SeasonDetailsState
import com.thomaskioko.tvmaniac.data.seasondetails.SeasonDetailsStateMachine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class SeasonDetailsViewModel(
    @Assisted savedStateHandle: SavedStateHandle,
    private val stateMachine: SeasonDetailsStateMachine
) : ViewModel() {

    private val showId: Long = savedStateHandle["showId"]!!

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
