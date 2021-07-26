package com.thomaskioko.tvmaniac.ui.discover

import com.thomaskioko.stargazer.core.presentation.ViewAction
import com.thomaskioko.stargazer.core.presentation.ViewState
import com.thomaskioko.tvmaniac.core.BaseViewModel
import com.thomaskioko.tvmaniac.core.annotations.DefaultDispatcher
import com.thomaskioko.tvmaniac.presentation.model.TvShow
import com.thomaskioko.tvmaniac.datasource.enums.TrendingDataRequest
import com.thomaskioko.tvmaniac.interactor.GetTrendingShowsInteractor
import com.thomaskioko.tvmaniac.util.DomainResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val interactor: GetTrendingShowsInteractor,
    @DefaultDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel<DiscoverShowsAction, DiscoverShowsState>(
    dispatcher = ioDispatcher,
    initialViewState = DiscoverShowsState.Loading
) {

    init {
        dispatchAction(
            DiscoverShowsAction.LoadTvShows(
                listOf(
                    TrendingDataRequest.FEATURED,
                    TrendingDataRequest.TODAY,
                    TrendingDataRequest.THIS_WEEK
                )
            )
        )
    }

    override fun handleAction(action: DiscoverShowsAction) {
        when (action) {
            is DiscoverShowsAction.LoadTvShows -> {
                interactor.invoke(action.trendingDataRequest)
                    .onEach { mutableViewState.emit(it.reduce()) }
                    .stateIn(
                        ioScope,
                        SharingStarted.Eagerly,
                        emptyMap<TrendingDataRequest, List<TvShow>>()
                    )
            }
        }
    }
}

private fun DomainResultState<LinkedHashMap<TrendingDataRequest, List<TvShow>>>.reduce(): DiscoverShowsState {
    return when (this) {
        is DomainResultState.Error -> DiscoverShowsState.Error(message)
        is DomainResultState.Loading -> DiscoverShowsState.Loading
        is DomainResultState.Success -> DiscoverShowsState.Success(data)
    }
}

sealed class DiscoverShowsState : ViewState {
    object Loading : DiscoverShowsState()
    data class Success(
        val dataMap: LinkedHashMap<TrendingDataRequest, List<TvShow>>
    ) : DiscoverShowsState()

    data class Error(val message: String = "") : DiscoverShowsState()
}

sealed class DiscoverShowsAction : ViewAction {
    data class LoadTvShows(
        val trendingDataRequest: List<TrendingDataRequest>
    ) : DiscoverShowsAction()
}