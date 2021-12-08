package com.thomaskioko.tvmaniac

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomaskioko.tvmaniac.core.Store
import com.thomaskioko.tvmaniac.core.discover.DiscoverShowAction
import com.thomaskioko.tvmaniac.core.discover.DiscoverShowEffect
import com.thomaskioko.tvmaniac.core.discover.DiscoverShowState
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory.FEATURED
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory.POPULAR
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory.TOP_RATED
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory.TRENDING
import com.thomaskioko.tvmaniac.datasource.repository.TrendingShowData
import com.thomaskioko.tvmaniac.interactor.GetDiscoverShowListInteractor
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
class DiscoverViewModel @Inject constructor(
    private val interactor: GetDiscoverShowListInteractor,
) : Store<DiscoverShowState, DiscoverShowAction, DiscoverShowEffect>, ViewModel() {

    private val state = MutableStateFlow(DiscoverShowState(false, emptyList()))
    private val sideEffect = MutableSharedFlow<DiscoverShowEffect>()

    init {
        dispatch(
            DiscoverShowAction.LoadTvShows(listOf(FEATURED, TRENDING, TOP_RATED, POPULAR))
        )
    }

    override fun observeState(): StateFlow<DiscoverShowState> = state

    override fun observeSideEffect(): Flow<DiscoverShowEffect> = sideEffect

    override fun dispatch(action: DiscoverShowAction) {
        val oldState = state.value

        when (action) {
            is DiscoverShowAction.LoadTvShows -> {
                viewModelScope.launch {
                    interactor.invoke(action.tvShowType)
                        .collect {
                            state.emit(it.stateReducer(oldState))
                        }
                }
            }
            is DiscoverShowAction.Error -> {
                viewModelScope.launch {
                    sideEffect.emit(DiscoverShowEffect.Error(action.message))
                }
            }
        }
    }

    private fun DomainResultState<List<TrendingShowData>>.stateReducer(
        state: DiscoverShowState,
    ): DiscoverShowState {
        return when (this) {
            is DomainResultState.Error -> {
                dispatch(DiscoverShowAction.Error(message))
                state.copy(isLoading = false)
            }
            is DomainResultState.Loading -> state.copy(isLoading = true)
            is DomainResultState.Success -> state.copy(isLoading = false, list = data)
        }
    }
}
