package com.thomaskioko.tvmaniac

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomaskioko.tvmaniac.core.Store
import com.thomaskioko.tvmaniac.core.discover.DiscoverShowAction
import com.thomaskioko.tvmaniac.core.discover.DiscoverShowAction.Error
import com.thomaskioko.tvmaniac.core.discover.DiscoverShowEffect
import com.thomaskioko.tvmaniac.core.discover.DiscoverShowState
import com.thomaskioko.tvmaniac.core.usecase.scope.CoroutineScopeOwner
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory.POPULAR
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory.TOP_RATED
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory.TRENDING
import com.thomaskioko.tvmaniac.interactor.ObserveShowsByCategoryInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    observeShow: ObserveShowsByCategoryInteractor,
) : Store<DiscoverShowState, DiscoverShowAction, DiscoverShowEffect>, CoroutineScopeOwner,
    ViewModel() {

    override val coroutineScope: CoroutineScope
        get() = viewModelScope

    private val uiStateFlow: StateFlow<DiscoverShowState> = combine(
        observeShow.invoke(TRENDING).distinctUntilChanged(),
        observeShow.invoke(TOP_RATED).distinctUntilChanged(),
        observeShow.invoke(POPULAR).distinctUntilChanged()
    ) { trendingShows, topRatedShows, popularShows ->

        DiscoverShowState(
            featuredShows = trendingShows.copy(
                shows = trendingShows.shows
                    .sortedBy { it.votes }
                    .take(5)
            ),
            trendingShows = trendingShows,
            topRatedShows = topRatedShows,
            popularShows = popularShows
        )
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DiscoverShowState.Empty,
    )

    private val sideEffect = MutableSharedFlow<DiscoverShowEffect>()

    override fun observeState(): StateFlow<DiscoverShowState> = uiStateFlow

    override fun observeSideEffect(): Flow<DiscoverShowEffect> = sideEffect

    override fun dispatch(action: DiscoverShowAction) {
        when (action) {
            is Error -> {
                coroutineScope.launch {
                    sideEffect.emit(DiscoverShowEffect.Error(action.message))
                }
            }
        }
    }
}
