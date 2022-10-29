package com.thomaskioko.tvmaniac.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomaskioko.tvmaniac.core.util.CoroutineScopeOwner
import com.thomaskioko.tvmaniac.shared.core.ui.Store
import com.thomaskioko.tvmaniac.shows.api.DiscoverShowAction
import com.thomaskioko.tvmaniac.shows.api.DiscoverShowAction.Error
import com.thomaskioko.tvmaniac.shows.api.DiscoverShowEffect
import com.thomaskioko.tvmaniac.shows.api.DiscoverShowState
import com.thomaskioko.tvmaniac.shows.api.UpdateShowsInteractor
import com.thomaskioko.tvmaniac.shows.api.ObserveShowsInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val updateShowsInteractor: UpdateShowsInteractor,
    private val observeShowsInteractor: ObserveShowsInteractor,
) : Store<DiscoverShowState, DiscoverShowAction, DiscoverShowEffect>,
    CoroutineScopeOwner,
    ViewModel() {

    override val coroutineScope: CoroutineScope
        get() = viewModelScope

    override val state: MutableStateFlow<DiscoverShowState> =
        MutableStateFlow(DiscoverShowState.Empty)

    private val sideEffect = MutableSharedFlow<DiscoverShowEffect>()

    init {
        coroutineScope.launch {
            dispatch(DiscoverShowAction.LoadTvShows)
        }

        observeShowResult()
    }

    override fun observeState(): StateFlow<DiscoverShowState> = state
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DiscoverShowState.Empty
        )


    override fun observeSideEffect(): Flow<DiscoverShowEffect> = sideEffect

    override fun dispatch(action: DiscoverShowAction) {
        when (action) {
            is DiscoverShowAction.LoadTvShows -> updateShowsInteractor.execute(Unit) {}
            is Error -> {
                coroutineScope.launch {
                    sideEffect.emit(DiscoverShowEffect.Error(action.message))
                }
            }
        }
    }

    private fun observeShowResult(){
        with(state) {
            observeShowsInteractor.execute(Unit) {
                onStart {
                    coroutineScope.launch {
                        emit(
                            value.copy(isLoading = true)
                        )
                    }
                }

                onNext {
                    coroutineScope.launch {
                        val newState = value.copy(
                            isLoading = false,
                            isEmpty = it.isEmpty,
                            featuredShows = it.featuredShows,
                            trendingShows = it.trendingShows,
                            recommendedShows = it.recommendedShows,
                            popularShows = it.popularShows,
                            anticipatedShows = it.anticipatedShows
                        )

                        emit(newState)
                    }
                }

                onError {
                    dispatch(Error(it.message ?: "Something went wrong"))
                }

            }
        }
    }
}
