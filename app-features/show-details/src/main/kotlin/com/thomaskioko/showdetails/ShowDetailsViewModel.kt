package com.thomaskioko.showdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomaskioko.tvmaniac.core.Effect
import com.thomaskioko.tvmaniac.interactor.EpisodesInteractor
import com.thomaskioko.tvmaniac.interactor.GetGenresInteractor
import com.thomaskioko.tvmaniac.interactor.GetShowInteractor
import com.thomaskioko.tvmaniac.interactor.SeasonsInteractor
import com.thomaskioko.tvmaniac.interactor.UpdateWatchlistInteractor
import com.thomaskioko.tvmaniac.presentation.model.GenreModel
import com.thomaskioko.tvmaniac.presentation.model.Season
import com.thomaskioko.tvmaniac.presentation.model.TrailerModel
import com.thomaskioko.tvmaniac.presentation.model.TvShow
import com.thomaskioko.tvmaniac.util.DomainResultState
import com.thomaskioko.tvmaniac.util.invoke
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(InternalCoroutinesApi::class)
@HiltViewModel
class ShowDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getShow: GetShowInteractor,
    seasonsInteractor: SeasonsInteractor,
    genresInteractor: GetGenresInteractor,
    private val episodeInteractor: EpisodesInteractor,
    private val updateWatchlistInteractor: UpdateWatchlistInteractor
) : ViewModel() {

    // TODO:: Refactor ViewModel to implement BaseViewmodel

    private val showId: Int = savedStateHandle.get("tvShowId")!!

    private val episodes = MutableStateFlow(EpisodesViewState())

    private val _uiEffects = MutableSharedFlow<DetailUiEffect>(extraBufferCapacity = 100)

    val uiEffects: Flow<DetailUiEffect>
        get() = _uiEffects.asSharedFlow()

    val uiStateFlow = combine(
        getShow(showId).distinctUntilChanged(),
        seasonsInteractor(showId).distinctUntilChanged(),
        genresInteractor().distinctUntilChanged(),
        episodes
    ) { showDetails, showSeasons, genreList, episodesState ->

        ShowDetailViewState(
            isLoading = showDetails.tvShowReducer().isLoading,
            tvShow = showDetails.tvShowReducer().tvShow,
            tvSeasons = showSeasons.seasonReducer().tvSeasons,
            genreList = genreList.genreReducer().genreList,
            episodesViewState = episodesState
        )
    }

    fun submitAction(action: ShowDetailAction) {
        when (action) {
            is ShowDetailAction.SeasonSelected -> fetchSeasonEpisodes(action)
            is ShowDetailAction.UpdateWatchlist -> updateWatchlist(action)
        }
    }

    private fun updateWatchlist(action: ShowDetailAction.UpdateWatchlist) {
        viewModelScope.launch {
            updateWatchlistInteractor.invoke(action.params)
                .onEach {
                    when (it) {
                        is DomainResultState.Error -> _uiEffects.emit(
                            DetailUiEffect.WatchlistError(
                                it.message
                            )
                        )
                        is DomainResultState.Loading -> Unit
                        is DomainResultState.Success -> Unit
                    }
                }
        }
    }

    private fun fetchSeasonEpisodes(action: ShowDetailAction.SeasonSelected) {
        viewModelScope.launch {
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
}

sealed class DetailUiEffect : Effect {
    data class WatchlistError(
        var errorMessage: String
    ) : DetailUiEffect()
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
