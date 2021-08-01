package com.thomaskioko.tvmaniac.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
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

    val uiStateFlow = combine(
        getShow(showId).distinctUntilChanged(),
        seasonsInteractor(showId).distinctUntilChanged(),
        genresInteractor().distinctUntilChanged(),
        episodes
    ) { showDetails, showSeasons, genreList, episodesState, ->

        ShowDetailViewState(
            isLoading = showDetails.tvShowReducer().isLoading,
            tvShow = showDetails.tvShowReducer().tvShow,
            tvSeasons = showSeasons.seasonReducer().tvSeasons,
            genreList = genreList.genreReducer().genreList,
            episodesViewState = episodesState
        )

    }


    fun submitAction(action: ShowDetailAction) {
        viewModelScope.launch {
            when (action) {
                is ShowDetailAction.SeasonSelected -> fetchSeasonEpisodes(action)
            }
        }
    }

    private suspend fun fetchSeasonEpisodes(
        action: ShowDetailAction.SeasonSelected,
    ) {
        episodeInteractor.invoke(action.query)
            .collect {
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

            }
    }
}

internal fun DomainResultState<TvShow>.tvShowReducer(): TvShowViewState {
    return when (this) {
        is DomainResultState.Error -> TvShowViewState(
            isLoading = false,
            errorMessage = message
        )
        is DomainResultState.Loading -> TvShowViewState(isLoading = true)
        is DomainResultState.Success -> TvShowViewState(
            isLoading = false,
            tvShow = data
        )
    }
}

internal fun DomainResultState<List<Season>>.seasonReducer(): SeasonViewState {
    return when (this) {
        is DomainResultState.Error -> SeasonViewState(
            isLoading = false,
            errorMessage = message
        )
        is DomainResultState.Loading -> SeasonViewState(isLoading = true)
        is DomainResultState.Success -> SeasonViewState(
            isLoading = false,
            tvSeasons = data
        )
    }
}

internal fun DomainResultState<List<GenreModel>>.genreReducer(): GenreViewState {
    return when (this) {
        is DomainResultState.Error -> GenreViewState(
            isLoading = false,
            errorMessage = message
        )
        is DomainResultState.Loading -> GenreViewState(isLoading = true)
        is DomainResultState.Success -> GenreViewState(
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

