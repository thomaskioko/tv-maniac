package com.thomaskioko.tvmaniac.ui.popularshows

import com.thomaskioko.stargazer.core.presentation.ViewAction
import com.thomaskioko.stargazer.core.presentation.ViewState
import com.thomaskioko.tvmaniac.core.BaseViewModel
import com.thomaskioko.tvmaniac.core.annotations.DefaultDispatcher
import com.thomaskioko.tvmaniac.datasource.cache.model.SeasonsEntity
import com.thomaskioko.tvmaniac.interactor.SeasonsInteractor
import com.thomaskioko.tvmaniac.util.DomainResultState
import com.thomaskioko.tvmaniac.util.DomainResultState.Error
import com.thomaskioko.tvmaniac.util.DomainResultState.Loading
import com.thomaskioko.tvmaniac.util.DomainResultState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SeasonsViewModel @Inject constructor(
    private val interactor: SeasonsInteractor,
    @DefaultDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel<SeasonsAction, SeasonsViewState>(
    initialViewState = SeasonsViewState.Loading,
    dispatcher = ioDispatcher
) {


    override fun handleAction(action: SeasonsAction) {
        when (action) {
            is SeasonsAction.LoadSeasons -> {
                interactor.invoke(action.tvShowId)
                    .onEach { mutableViewState.emit(it.reduce()) }
                    .stateIn(ioScope, SharingStarted.Eagerly, emptyList<SeasonsEntity>())
            }
        }
    }
}

private fun DomainResultState<List<SeasonsEntity>>.reduce(): SeasonsViewState {
    return when (this) {
        is Error -> SeasonsViewState.Error(message)
        is Loading -> SeasonsViewState.Loading
        is Success -> SeasonsViewState.Success(data)
    }
}

sealed class SeasonsAction : ViewAction {
    data class LoadSeasons(val tvShowId: Int) : SeasonsAction()
}

sealed class SeasonsViewState : ViewState {
    object Loading : SeasonsViewState()
    data class Success(val data: List<SeasonsEntity>) : SeasonsViewState()
    data class Error(val message: String = "") : SeasonsViewState()
}