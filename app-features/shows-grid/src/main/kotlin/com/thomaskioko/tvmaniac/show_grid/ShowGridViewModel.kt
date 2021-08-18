package com.thomaskioko.tvmaniac.show_grid

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomaskioko.tvmaniac.core.Store
import com.thomaskioko.tvmaniac.interactor.GetShowsByCategoryInteractor
import com.thomaskioko.tvmaniac.presentation.contract.ShowsGridAction
import com.thomaskioko.tvmaniac.presentation.contract.ShowsGridAction.Error
import com.thomaskioko.tvmaniac.presentation.contract.ShowsGridAction.LoadTvShows
import com.thomaskioko.tvmaniac.presentation.contract.ShowsGridEffect
import com.thomaskioko.tvmaniac.presentation.contract.ShowsGridState
import com.thomaskioko.tvmaniac.presentation.model.TvShow
import com.thomaskioko.tvmaniac.util.DomainResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShowGridViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val interactor: GetShowsByCategoryInteractor
) : Store<ShowsGridState, ShowsGridAction, ShowsGridEffect>, ViewModel() {

    val showType: Int = savedStateHandle.get("showType")!!

    private val state = MutableStateFlow(ShowsGridState.Empty)
    private val sideEffect = MutableSharedFlow<ShowsGridEffect>()

    init {
        dispatch(LoadTvShows)
    }

    override fun observeState(): StateFlow<ShowsGridState> = state

    override fun observeSideEffect(): Flow<ShowsGridEffect> = sideEffect

    override fun dispatch(action: ShowsGridAction) {
        val oldState = state.value

        when (action) {
            is Error -> {
                viewModelScope.launch {
                    sideEffect.emit(ShowsGridEffect.Error(action.message))
                }
            }
            is LoadTvShows -> {
                viewModelScope.launch {
                    interactor.invoke(showType)
                        .collect {
                            state.emit(it.stateReducer(oldState))
                        }
                }
            }
        }
    }

    private fun DomainResultState<List<TvShow>>.stateReducer(
        state: ShowsGridState,
    ): ShowsGridState {
        return when (this) {
            is DomainResultState.Error -> {
                dispatch(Error(message))
                state.copy(isLoading = false)
            }
            is DomainResultState.Loading -> state.copy(isLoading = true)
            is DomainResultState.Success -> state.copy(isLoading = false, list = data)
        }
    }
}