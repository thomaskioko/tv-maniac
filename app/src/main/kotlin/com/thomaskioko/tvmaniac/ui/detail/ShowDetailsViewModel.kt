package com.thomaskioko.tvmaniac.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.thomaskioko.tvmaniac.core.annotations.DefaultDispatcher
import com.thomaskioko.tvmaniac.interactor.EpisodeQuery
import com.thomaskioko.tvmaniac.interactor.EpisodesInteractor
import com.thomaskioko.tvmaniac.interactor.GetShowInteractor
import com.thomaskioko.tvmaniac.interactor.SeasonsInteractor
import com.thomaskioko.tvmaniac.presentation.model.Episode
import com.thomaskioko.tvmaniac.presentation.model.Season
import com.thomaskioko.tvmaniac.presentation.model.TvShow
import com.thomaskioko.tvmaniac.util.DomainResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShowDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getShow: GetShowInteractor,
    seasonsInteractor: SeasonsInteractor,
    private val episodeInteractor: EpisodesInteractor,
    @DefaultDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    //TODO:: Refactor ViewModel to implement BaseViewmodel

    private val showId: Int = savedStateHandle.get("tvShowId")!!

    private val episodes = MutableStateFlow(emptyList<Episode>())

    private val viewModelJob = SupervisorJob()
    val ioScope = CoroutineScope(ioDispatcher + viewModelJob)

    val uiStateFlow = combine(
        getShow.invoke(showId).distinctUntilChanged(),
        seasonsInteractor.invoke(showId).distinctUntilChanged(),
        episodes
    ) { showDetails, showSeasons, episodes ->
        ShowDetailViewState(
            isLoading = showDetails.tvShowReducer().isLoading,
            tvShow = showDetails.tvShowReducer().tvShow,
            tvSeasons = showSeasons.seasonReducer().tvSeasons,
            episodeList = episodes
        )
    }


    fun submitAction(action: ShowDetailAction) {
        ioScope.launch {
            when (action) {
                is ShowDetailAction.SeasonSelected -> fetchSeasonEpisodes(action)
            }
        }
    }

    private fun fetchSeasonEpisodes(
        action: ShowDetailAction.SeasonSelected,
    ) {
        episodeInteractor.invoke(action.query)
            .onEach {
                when (it) {
                    is DomainResultState.Error -> {
                        //TODO:: Pass the error down
                    }
                    is DomainResultState.Loading -> {
                        //TODO:: Pass state down
                    }
                    is DomainResultState.Success -> episodes.value = it.data
                }

            }.launchIn(ioScope)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}

internal fun DomainResultState<TvShow>.tvShowReducer(): ShowDetailViewState {
    return when (this) {
        is DomainResultState.Error -> ShowDetailViewState(
            isLoading = false,
            errorMessage = message
        )
        is DomainResultState.Loading -> ShowDetailViewState(isLoading = true)
        is DomainResultState.Success -> ShowDetailViewState(
            isLoading = false,
            tvShow = data
        )
    }
}

internal fun DomainResultState<List<Season>>.seasonReducer(): ShowDetailViewState {
    return when (this) {
        is DomainResultState.Error -> ShowDetailViewState(
            isLoading = false,
            errorMessage = message
        )
        is DomainResultState.Loading -> ShowDetailViewState(isLoading = true)
        is DomainResultState.Success -> ShowDetailViewState(
            isLoading = false,
            tvSeasons = data
        )
    }
}

sealed class ShowDetailAction {
    data class SeasonSelected(
        val query: EpisodeQuery
    ) : ShowDetailAction()
}


data class ShowDetailViewState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val tvShow: TvShow = TvShow.EMPTY_SHOW,
    val tvSeasons: List<Season> = emptyList(),
    val episodeList: List<Episode> = emptyList(),
) {
    companion object {
        val Empty = ShowDetailViewState()
    }
}