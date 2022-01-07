package com.thomaskioko.tvmaniac

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomaskioko.tvmaniac.interactor.ObserveDiscoverShowsInteractor
import com.thomaskioko.tvmaniac.presentation.contract.DiscoverShowAction
import com.thomaskioko.tvmaniac.presentation.contract.DiscoverShowAction.Error
import com.thomaskioko.tvmaniac.presentation.contract.DiscoverShowEffect
import com.thomaskioko.tvmaniac.presentation.contract.DiscoverShowResult
import com.thomaskioko.tvmaniac.presentation.contract.DiscoverShowState
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
class DiscoverViewModel @Inject constructor(
    private val observeDiscoverShow: ObserveDiscoverShowsInteractor,
) : Store<DiscoverShowState, DiscoverShowAction, DiscoverShowEffect>,
    CoroutineScopeOwner,
    ViewModel() {

    override val coroutineScope: CoroutineScope
        get() = viewModelScope

    private val state = MutableStateFlow(DiscoverShowState(false, DiscoverShowResult.EMPTY))

    private val sideEffect = MutableSharedFlow<DiscoverShowEffect>()

    init {
        dispatch(DiscoverShowAction.LoadTvShows)
    }

    override fun observeState(): StateFlow<DiscoverShowState> = state

    override fun observeSideEffect(): Flow<DiscoverShowEffect> = sideEffect

    override fun dispatch(action: DiscoverShowAction) {
        val oldState = state.value

        when (action) {
            is DiscoverShowAction.LoadTvShows -> {
                with(state) {
                    observeDiscoverShow.execute(Unit) {
                        onStart {
                            coroutineScope.launch { emit(oldState.copy(isLoading = false)) }
                        }

                        onNext {
                            coroutineScope.launch {
                                emit(
                                    oldState.copy(
                                        isLoading = false,
                                        showData = it
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
            is Error -> {
                coroutineScope.launch {
                    sideEffect.emit(DiscoverShowEffect.Error(action.message))
                }
            }
        }
    }
}
