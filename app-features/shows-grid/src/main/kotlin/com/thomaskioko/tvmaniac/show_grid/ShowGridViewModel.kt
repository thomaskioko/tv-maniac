package com.thomaskioko.tvmaniac.show_grid

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuuurt.paging.multiplatform.PagingData
import com.thomaskioko.tvmaniac.core.Store
import com.thomaskioko.tvmaniac.interactor.GetShowsByCategoryInteractor
import com.thomaskioko.tvmaniac.presentation.contract.ShowsGridAction
import com.thomaskioko.tvmaniac.presentation.contract.ShowsGridAction.Error
import com.thomaskioko.tvmaniac.presentation.contract.ShowsGridAction.LoadTvShows
import com.thomaskioko.tvmaniac.presentation.contract.ShowsGridEffect
import com.thomaskioko.tvmaniac.presentation.contract.ShowsGridState
import com.thomaskioko.tvmaniac.presentation.model.TvShow
import com.thomaskioko.tvmaniac.util.CommonFlow
import com.thomaskioko.tvmaniac.util.DomainResultState
import com.thomaskioko.tvmaniac.util.DomainResultState.Loading
import com.thomaskioko.tvmaniac.util.DomainResultState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
        when (action) {
            is Error -> {
                viewModelScope.launch {
                    sideEffect.emit(ShowsGridEffect.Error(action.message))
                }
            }
            is LoadTvShows -> {
                interactor.invoke(showType)
                    .onEach { state.emit(it.reducer(state.value)) }
                    .launchIn(viewModelScope)
            }
        }
    }

    private fun DomainResultState<CommonFlow<PagingData<TvShow>>>.reducer(
        state: ShowsGridState
    ): ShowsGridState {
        return when (this) {
            is DomainResultState.Error -> {
                dispatch(Error(message))
                state.copy(isLoading = false)
            }
            is Loading -> state.copy(isLoading = true)
            is Success -> state.copy(isLoading = false, list = data)
        }
    }
}
