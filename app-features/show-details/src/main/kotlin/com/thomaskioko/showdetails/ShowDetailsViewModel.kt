package com.thomaskioko.showdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomaskioko.showdetails.DetailUiEffect.WatchlistError
import com.thomaskioko.tvmaniac.core.Effect
import com.thomaskioko.tvmaniac.core.usecase.invoke
import com.thomaskioko.tvmaniac.core.usecase.scope.CoroutineScopeOwner
import com.thomaskioko.tvmaniac.interactor.EpisodesInteractor
import com.thomaskioko.tvmaniac.interactor.GetGenresInteractor
import com.thomaskioko.tvmaniac.interactor.GetShowInteractor
import com.thomaskioko.tvmaniac.interactor.SeasonsInteractor
import com.thomaskioko.tvmaniac.interactor.UpdateWatchlistInteractor
import com.thomaskioko.tvmaniac.presentation.model.GenreModel
import com.thomaskioko.tvmaniac.presentation.model.TvShow
import com.thomaskioko.tvmaniac.util.DomainResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(InternalCoroutinesApi::class)
@HiltViewModel
class ShowDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getShow: GetShowInteractor,
    private val seasonsInteractor: SeasonsInteractor,
    genresInteractor: GetGenresInteractor,
    private val episodeInteractor: EpisodesInteractor,
    private val updateWatchlistInteractor: UpdateWatchlistInteractor
) : CoroutineScopeOwner, ViewModel() {

    override val coroutineScope: CoroutineScope
        get() = viewModelScope

    private val showId: Int = savedStateHandle.get("tvShowId")!!

    private val episodes = MutableStateFlow(EpisodesViewState())
    private val seasonViewState = MutableStateFlow(SeasonViewState())

    private val _uiEffects = MutableSharedFlow<DetailUiEffect>(extraBufferCapacity = 100)

    val uiEffects: Flow<DetailUiEffect>
        get() = _uiEffects.asSharedFlow()

    val uiStateFlow = combine(
        getShow(showId).distinctUntilChanged(),
        genresInteractor().distinctUntilChanged(),
        seasonViewState,
        episodes
    ) { showDetails, genreList, showSeasons, episodesState ->

        ShowDetailViewState(
            isLoading = showDetails.tvShowReducer().isLoading,
            tvShow = showDetails.tvShowReducer().tvShow,
            genreList = genreList.genreReducer().genreList,
            tvSeasons = showSeasons.tvSeasons,
            episodesViewState = episodesState
        )
    }

    init {
        fetchSeason()
    }

    fun submitAction(action: ShowDetailAction) {
        when (action) {
            is ShowDetailAction.SeasonSelected -> fetchEpisodes(action)
            is ShowDetailAction.UpdateWatchlist -> updateWatchlist(action)
        }
    }

    private fun updateWatchlist(action: ShowDetailAction.UpdateWatchlist) {
        updateWatchlistInteractor.execute(action.params) {
            onError {
                coroutineScope.launch {
                    _uiEffects.emit(
                        WatchlistError(it.message ?: "Something went wrong")
                    )
                }
            }
        }
    }

    private fun fetchSeason() {
        val oldState = seasonViewState.value
        with(seasonViewState) {
            seasonsInteractor.execute(showId) {
                onStart {
                    coroutineScope.launch {
                        emit(
                            oldState.copy(
                                isLoading = false,
                            )
                        )
                    }
                }
                onNext {
                    coroutineScope.launch {
                        emit(
                            oldState.copy(
                                isLoading = false,
                                tvSeasons = it
                            )
                        )
                    }
                }
                onError {
                    coroutineScope.launch {
                        emit(
                            oldState.copy(
                                isLoading = false,
                                errorMessage = it.message ?: "Something went wrong"
                            )
                        )
                    }
                }
            }
        }
    }

    private fun fetchEpisodes(action: ShowDetailAction.SeasonSelected) {
        with(episodes) {
            episodeInteractor.execute(action.query) {
                onStart { coroutineScope.launch { value = EpisodesViewState(isLoading = true) } }
                onNext {
                    coroutineScope.launch {
                        value = EpisodesViewState(
                            isLoading = false,
                            episodeList = it
                        )
                    }
                }
                onError {
                    coroutineScope.launch {
                        value = EpisodesViewState(
                            isLoading = false,
                            errorMessage = it.message ?: "Something went wrong"
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
