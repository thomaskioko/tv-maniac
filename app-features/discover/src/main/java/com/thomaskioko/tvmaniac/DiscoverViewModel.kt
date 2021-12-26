package com.thomaskioko.tvmaniac

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomaskioko.tvmaniac.core.Store
import com.thomaskioko.tvmaniac.core.discover.DiscoverShowAction
import com.thomaskioko.tvmaniac.core.discover.DiscoverShowAction.Error
import com.thomaskioko.tvmaniac.core.discover.DiscoverShowAction.LoadTvShows
import com.thomaskioko.tvmaniac.core.discover.DiscoverShowEffect
import com.thomaskioko.tvmaniac.core.discover.DiscoverShowState
import com.thomaskioko.tvmaniac.core.usecase.scope.CoroutineScopeOwner
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory.FEATURED
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory.POPULAR
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory.TOP_RATED
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory.TRENDING
import com.thomaskioko.tvmaniac.interactor.GetDiscoverShowListInteractor
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
    private val interactor: GetDiscoverShowListInteractor,
) : Store<DiscoverShowState, DiscoverShowAction, DiscoverShowEffect>,
    CoroutineScopeOwner, ViewModel() {

    override val coroutineScope: CoroutineScope
        get() = viewModelScope

    private val state = MutableStateFlow(DiscoverShowState(false, emptyList()))
    private val sideEffect = MutableSharedFlow<DiscoverShowEffect>()

    init {
        dispatch(LoadTvShows(listOf(FEATURED, TRENDING, TOP_RATED, POPULAR)))
    }

    override fun observeState(): StateFlow<DiscoverShowState> = state

    override fun observeSideEffect(): Flow<DiscoverShowEffect> = sideEffect

    override fun dispatch(action: DiscoverShowAction) {
        val oldState = state.value

        when (action) {
            is LoadTvShows -> {
                with(state) {
                    interactor.execute(action.tvShowType) {
                        onStart {
                            coroutineScope.launch { emit(oldState.copy(isLoading = true)) }
                        }

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
            is Error -> {
                coroutineScope.launch {
                    sideEffect.emit(DiscoverShowEffect.Error(action.message))
                }
            }
        }
    }
}
