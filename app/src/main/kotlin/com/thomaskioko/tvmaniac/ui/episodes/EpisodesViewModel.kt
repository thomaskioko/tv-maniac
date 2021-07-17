package com.thomaskioko.tvmaniac.ui.episodes

import com.thomaskioko.stargazer.core.presentation.ViewAction
import com.thomaskioko.stargazer.core.presentation.ViewState
import com.thomaskioko.tvmaniac.core.BaseViewModel
import com.thomaskioko.tvmaniac.core.annotations.DefaultDispatcher
import com.thomaskioko.tvmaniac.datasource.cache.model.EpisodeEntity
import com.thomaskioko.tvmaniac.datasource.cache.model.SeasonsEntity
import com.thomaskioko.tvmaniac.interactor.EpisodeQuery
import com.thomaskioko.tvmaniac.interactor.EpisodesInteractor
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
class EpisodesViewModel @Inject constructor(
    private val interactor: EpisodesInteractor,
    @DefaultDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel<EpisodesAction, EpisodesViewState>(
    initialViewState = EpisodesViewState.Loading,
    dispatcher = ioDispatcher
) {


    override fun handleAction(action: EpisodesAction) {
        when (action) {
            is EpisodesAction.LoadEpisodes -> {
                interactor.invoke(action.query)
                    .onEach { mutableViewState.emit(it.reduce()) }
                    .stateIn(ioScope, SharingStarted.Eagerly, emptyList<SeasonsEntity>())
            }
        }
    }
}

private fun DomainResultState<List<EpisodeEntity>>.reduce(): EpisodesViewState {
    return when (this) {
        is Error -> EpisodesViewState.Error(message)
        is Loading -> EpisodesViewState.Loading
        is Success -> EpisodesViewState.Success(data)
    }
}

sealed class EpisodesAction : ViewAction {
    data class LoadEpisodes(val query: EpisodeQuery) : EpisodesAction()
}

sealed class EpisodesViewState : ViewState {
    object Loading : EpisodesViewState()
    data class Success(val data: List<EpisodeEntity>) : EpisodesViewState()
    data class Error(val message: String = "") : EpisodesViewState()
}