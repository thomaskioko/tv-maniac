package com.thomaskioko.tvmaniac.show_grid

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomaskioko.tvmaniac.interactor.GetShowsByCategoryInteractor
import com.thomaskioko.tvmaniac.presentation.contract.ShowsGridAction
import com.thomaskioko.tvmaniac.presentation.contract.ShowsGridAction.Error
import com.thomaskioko.tvmaniac.presentation.contract.ShowsGridAction.LoadTvShows
import com.thomaskioko.tvmaniac.presentation.contract.ShowsGridEffect
import com.thomaskioko.tvmaniac.presentation.contract.ShowsGridState
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
class ShowGridViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val interactor: GetShowsByCategoryInteractor
) : Store<ShowsGridState, ShowsGridAction, ShowsGridEffect>,
    CoroutineScopeOwner, ViewModel() {

    override val coroutineScope: CoroutineScope
        get() = viewModelScope

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
                with(state) {
                    interactor.execute(showType) {
                        onStart { coroutineScope.launch { emit(oldState.copy(isLoading = true)) } }
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
                            dispatch(Error(it.message ?: "Something went wrong"))
                        }
                    }
                }
            }
        }
    }
}
