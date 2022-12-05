package com.thomaskioko.tvmaniac.show_grid

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomaskioko.tvmaniac.core.util.CoroutineScopeOwner
import com.thomaskioko.tvmaniac.shared.core.ui.Store
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
    private val interactor: ObservePagedShowsByCategoryInteractor
) : Store<ShowsLoaded, ShowsGridAction, ShowsGridEffect>,
    CoroutineScopeOwner, ViewModel() {

    private val sideEffect = MutableSharedFlow<ShowsGridEffect>()
    val showType: Int = savedStateHandle["showType"]!!

    override val coroutineScope: CoroutineScope
        get() = viewModelScope

    override val state = MutableStateFlow(ShowsLoaded.Empty)

    init {
        dispatch(ShowsGridAction.LoadTvShows)
    }

    override fun observeState(): StateFlow<ShowsLoaded> = state

    override fun observeSideEffect(): Flow<ShowsGridEffect> = sideEffect

    override fun dispatch(action: ShowsGridAction) {
        val oldState = state.value
        when (action) {
            is ShowsGridAction.Error -> {
                viewModelScope.launch {
                    sideEffect.emit(ShowsGridEffect.Error(action.message))
                }
            }
            is ShowsGridAction.LoadTvShows -> {
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
                            dispatch(ShowsGridAction.Error(it.message ?: "Something went wrong"))
                        }
                    }
                }
            }
        }
    }
}
