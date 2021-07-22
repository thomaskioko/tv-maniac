package com.thomaskioko.tvmaniac.ui.detail

import com.thomaskioko.stargazer.core.presentation.ViewAction
import com.thomaskioko.stargazer.core.presentation.ViewState
import com.thomaskioko.tvmaniac.core.BaseViewModel
import com.thomaskioko.tvmaniac.core.annotations.DefaultDispatcher
import com.thomaskioko.tvmaniac.datasource.cache.model.TvShow
import com.thomaskioko.tvmaniac.interactor.GetShowInteractor
import com.thomaskioko.tvmaniac.interactor.SeasonsInteractor
import com.thomaskioko.tvmaniac.util.DomainResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ShowDetailsViewModel @Inject constructor(
    private val interactor: GetShowInteractor,
    private val seasonsInteractor: SeasonsInteractor,
    @DefaultDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel<ShowDetailsAction, ShowDetailsViewState>(
    initialViewState = ShowDetailsViewState.Loading,
    dispatcher = ioDispatcher
) {

    override fun handleAction(action: ShowDetailsAction) {
        when (action) {
            is ShowDetailsAction.LoadShowDetails -> {
                interactor.invoke(action.tvShowId)
                    .onEach { mutableViewState.emit(it.reduce()) }
                    .stateIn(ioScope, SharingStarted.Eagerly, TvShow.EMPTY_SHOW)
            }
        }
    }
}


internal fun DomainResultState<TvShow>.reduce(): ShowDetailsViewState {
    return when (this) {
        is DomainResultState.Error -> ShowDetailsViewState.Error(message)
        is DomainResultState.Loading -> ShowDetailsViewState.Loading
        is DomainResultState.Success -> ShowDetailsViewState.Success(data)
    }
}

sealed class ShowDetailsAction : ViewAction {
    data class LoadShowDetails(val tvShowId: Int) : ShowDetailsAction()
}

sealed class ShowDetailsViewState : ViewState {
    object Loading : ShowDetailsViewState()
    data class Success(val data: TvShow) : ShowDetailsViewState()
    data class Error(val message: String = "") : ShowDetailsViewState()
}