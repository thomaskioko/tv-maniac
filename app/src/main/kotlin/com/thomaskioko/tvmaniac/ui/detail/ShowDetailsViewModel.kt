package com.thomaskioko.tvmaniac.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.thomaskioko.tvmaniac.core.annotations.DefaultDispatcher
import com.thomaskioko.tvmaniac.interactor.EpisodesInteractor
import com.thomaskioko.tvmaniac.interactor.GetGenresInteractor
import com.thomaskioko.tvmaniac.interactor.GetShowInteractor
import com.thomaskioko.tvmaniac.interactor.GetTrailersInteractor
import com.thomaskioko.tvmaniac.interactor.SeasonsInteractor
import com.thomaskioko.tvmaniac.presentation.model.GenreModel
import com.thomaskioko.tvmaniac.presentation.model.Season
import com.thomaskioko.tvmaniac.presentation.model.TrailerModel
import com.thomaskioko.tvmaniac.presentation.model.TvShow
import com.thomaskioko.tvmaniac.util.DomainResultState
import com.thomaskioko.tvmaniac.util.invoke
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
    genresInteractor: GetGenresInteractor,
    trailersInteractor: GetTrailersInteractor,
    private val episodeInteractor: EpisodesInteractor,
    @DefaultDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    //TODO:: Refactor ViewModel to implement BaseViewmodel

    private val showId: Int = savedStateHandle.get("tvShowId")!!

    private val episodes = MutableStateFlow(EpisodesViewState())

    private val viewModelJob = SupervisorJob()
    val ioScope = CoroutineScope(ioDispatcher + viewModelJob)

    val uiStateFlow = combine(
        getShow(showId).distinctUntilChanged(),
        seasonsInteractor(showId).distinctUntilChanged(),
        genresInteractor().distinctUntilChanged(),
        trailersInteractor(showId).distinctUntilChanged(),
        episodes
    ) { showDetails, showSeasons, genreList, trailerState, episodesState, ->
        ShowDetailViewState(
            isLoading = showDetails.tvShowReducer().isLoading,
            tvShow = showDetails.tvShowReducer().tvShow,
            tvSeasons = showSeasons.seasonReducer().tvSeasons,
            genreList = genreList.genreReducer().genreList,
            trailerViewState = trailerState.trailersReducer(),
            episodesViewState = episodesState
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
                        episodes.value = EpisodesViewState(
                            isLoading = false,
                            errorMessage = it.message
                        )
                    }
                    is DomainResultState.Loading -> {
                        episodes.value = EpisodesViewState(isLoading = true)
                    }
                    is DomainResultState.Success -> {
                        episodes.value = EpisodesViewState(
                            isLoading = false,
                            episodeList = it.data
                        )
                    }
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

internal fun DomainResultState<List<GenreModel>>.genreReducer(): ShowDetailViewState {
    return when (this) {
        is DomainResultState.Error -> ShowDetailViewState(
            isLoading = false,
            errorMessage = message
        )
        is DomainResultState.Loading -> ShowDetailViewState(isLoading = true)
        is DomainResultState.Success -> ShowDetailViewState(
            isLoading = false,
            genreList = data
        )
    }
}

internal fun DomainResultState<List<TrailerModel>>.trailersReducer(): TrailersViewState {
    return when (this) {
        is DomainResultState.Error -> TrailersViewState(
            isLoading = false,
            errorMessage = message
        )
        is DomainResultState.Loading -> TrailersViewState(isLoading = true)
        is DomainResultState.Success -> TrailersViewState(
            isLoading = false,
            trailerList = data
        )
    }
}

