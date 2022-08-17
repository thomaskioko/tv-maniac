package com.thomaskioko.tvmaniac.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomaskioko.tvmaniac.core.util.CoroutineScopeOwner
import com.thomaskioko.tvmaniac.discover.api.DataLoaded
import com.thomaskioko.tvmaniac.discover.api.DiscoverShowAction
import com.thomaskioko.tvmaniac.discover.api.DiscoverShowAction.Error
import com.thomaskioko.tvmaniac.discover.api.DiscoverShowEffect
import com.thomaskioko.tvmaniac.discover.api.DiscoverShowState
import com.thomaskioko.tvmaniac.discover.api.ErrorState
import com.thomaskioko.tvmaniac.discover.api.Loading
import com.thomaskioko.tvmaniac.discover.api.ObserveDiscoverShowsInteractor
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
class DiscoverViewModel @Inject constructor(
    private val observeDiscoverShow: ObserveDiscoverShowsInteractor,
) : Store<DiscoverShowState, DiscoverShowAction, DiscoverShowEffect>,
    CoroutineScopeOwner,
    ViewModel() {

    override val coroutineScope: CoroutineScope
        get() = viewModelScope

    override val state: MutableStateFlow<DiscoverShowState> = MutableStateFlow(Loading)

    private val sideEffect = MutableSharedFlow<DiscoverShowEffect>()

    init {
        coroutineScope.launch {
            dispatch(DiscoverShowAction.LoadTvShows)
        }
    }

    override fun observeState(): StateFlow<DiscoverShowState> = state

    override fun observeSideEffect(): Flow<DiscoverShowEffect> = sideEffect

    override fun dispatch(action: DiscoverShowAction) {
        when (action) {
            is DiscoverShowAction.LoadTvShows -> {
                with(state) {
                    observeDiscoverShow.execute(Unit) {
                        onStart {
                            coroutineScope.launch { emit(Loading) }
                        }

                        onNext {
                            coroutineScope.launch {
                                dispatch(
                                    DiscoverShowAction.DataLoaded(
                                        showData = it
                                    )

                                )
                            }
                        }

                        onError {
                            dispatch(Error(it.message ?: "Something went wrong"))
                        }
                    }
                }
            }
            is Error -> {
                coroutineScope.launch {
                    state.emit(ErrorState(action.message))
                }
            }
            is DiscoverShowAction.DataLoaded -> {
                coroutineScope.launch {
                    state.emit(
                        DataLoaded(
                            showData = action.showData
                        )
                    )
                }
            }
        }
    }
}
