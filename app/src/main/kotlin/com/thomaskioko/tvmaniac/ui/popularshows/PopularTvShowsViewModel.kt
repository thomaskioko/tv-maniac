package com.thomaskioko.tvmaniac.ui.popularshows

import com.thomaskioko.stargazer.core.presentation.ViewAction
import com.thomaskioko.stargazer.core.presentation.ViewState
import com.thomaskioko.tvmaniac.core.BaseViewModel
import com.thomaskioko.tvmaniac.core.annotations.DefaultDispatcher
import com.thomaskioko.tvmaniac.presentation.model.TvShow
import com.thomaskioko.tvmaniac.interactor.PopularShowsInteractor
import com.thomaskioko.tvmaniac.util.DomainResultState
import com.thomaskioko.tvmaniac.util.DomainResultState.Error
import com.thomaskioko.tvmaniac.util.DomainResultState.Loading
import com.thomaskioko.tvmaniac.util.DomainResultState.Success
import com.thomaskioko.tvmaniac.util.invoke
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PopularTvShowsViewModel @Inject constructor(
    private val popularShowsInteractor: PopularShowsInteractor,
    @DefaultDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel<PopularShowsAction, PopularShowsState>(
    initialViewState = PopularShowsState.Loading,
    dispatcher = ioDispatcher
) {


    override fun handleAction(action: PopularShowsAction) {
        when (action) {
            PopularShowsAction.LoadPopularTvShows -> {
                popularShowsInteractor.invoke()
                    .onEach { mutableViewState.emit(it.reduce()) }
                    .stateIn(ioScope, SharingStarted.Eagerly, emptyList<TvShow>())
            }
        }
    }
}

private fun DomainResultState<List<TvShow>>.reduce(): PopularShowsState {
    return when(this){
        is Error -> PopularShowsState.Error(message)
        is Loading -> PopularShowsState.Loading
        is Success -> PopularShowsState.Success(data)
    }
}

sealed class PopularShowsAction : ViewAction {
    object LoadPopularTvShows : PopularShowsAction()
}

sealed class PopularShowsState : ViewState {
    object Loading : PopularShowsState()
    data class Success(val list: List<TvShow>) : PopularShowsState()
    data class Error(val message: String = "") : PopularShowsState()
}