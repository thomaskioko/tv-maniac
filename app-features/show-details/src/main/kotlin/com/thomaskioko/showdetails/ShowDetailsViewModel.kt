package com.thomaskioko.showdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomaskioko.showdetails.ShowDetailAction.SeasonSelected
import com.thomaskioko.showdetails.ShowDetailAction.UpdateWatchlist
import com.thomaskioko.showdetails.ShowDetailEffect.ShowDetailsError
import com.thomaskioko.tvmaniac.episodes.api.EpisodeQuery
import com.thomaskioko.tvmaniac.episodes.api.EpisodesInteractor
import com.thomaskioko.tvmaniac.genre.api.GetGenresInteractor
import com.thomaskioko.tvmaniac.interactor.GetShowInteractor
import com.thomaskioko.tvmaniac.interactor.UpdateWatchlistInteractor
import com.thomaskioko.tvmaniac.seasons.api.interactor.SeasonsInteractor
import com.thomaskioko.tvmaniac.shared.core.CoroutineScopeOwner
import com.thomaskioko.tvmaniac.shared.core.store.Store
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(InternalCoroutinesApi::class)
@HiltViewModel
class ShowDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getShow: GetShowInteractor,
    private val seasonsInteractor: SeasonsInteractor,
    private val genresInteractor: com.thomaskioko.tvmaniac.genre.api.GetGenresInteractor,
    private val episodeInteractor: EpisodesInteractor,
    private val updateWatchlistInteractor: UpdateWatchlistInteractor
) : Store<ShowDetailViewState, ShowDetailAction, ShowDetailEffect>,
    CoroutineScopeOwner,
    ViewModel() {

    override val coroutineScope: CoroutineScope
        get() = viewModelScope

    private val showId: Int = savedStateHandle.get("tvShowId")!!

    private val state = MutableStateFlow(ShowDetailViewState.Empty)

    private val uiEffects = MutableSharedFlow<ShowDetailEffect>(extraBufferCapacity = 100)

    init {
        dispatch(ShowDetailAction.LoadShowDetails)
        dispatch(ShowDetailAction.LoadSeasons)
        dispatch(ShowDetailAction.LoadGenres)
    }

    override fun observeState(): StateFlow<ShowDetailViewState> = state

    override fun observeSideEffect(): Flow<ShowDetailEffect> = uiEffects

    override fun dispatch(action: ShowDetailAction) {
        when (action) {
            ShowDetailAction.LoadSeasons -> fetchSeason()
            ShowDetailAction.LoadGenres -> fetchGenres()
            ShowDetailAction.LoadShowDetails -> loadShowDetails()
            is SeasonSelected -> fetchEpisodes(action.query)
            is UpdateWatchlist -> updateWatchlist(action)
            is ShowDetailAction.Error -> {
                coroutineScope.launch {
                    uiEffects.emit(ShowDetailsError(action.message))
                }
            }
            is ShowDetailAction.LoadEpisodes -> fetchEpisodes(action.query)
        }
    }

    private fun loadShowDetails() {
        with(state) {
            getShow.execute(showId) {
                onStart {
                    coroutineScope.launch { emit(value.copy(isLoading = false)) }
                }
                onNext {
                    coroutineScope.launch {
                        emit(
                            value.copy(
                                isLoading = false,
                                showUiModel = it
                            )
                        )
                    }
                }
                onError {
                    coroutineScope.launch {
                        emit(
                            value.copy(
                                isLoading = false,
                                errorMessage = it.message ?: "Something went wrong"
                            )
                        )
                    }
                    dispatch(ShowDetailAction.Error(it.message ?: "Something went wrong"))
                }
            }
        }
    }

    private fun fetchGenres() {
        with(state) {
            genresInteractor.execute(Unit) {
                onStart {
                    coroutineScope.launch { emit(value.copy(isLoading = false)) }
                }
                onNext {
                    coroutineScope.launch {
                        emit(
                            value.copy(
                                isLoading = false,
                                genreUIList = it
                            )
                        )
                    }
                }
                onError {
                    coroutineScope.launch {
                        emit(
                            value.copy(
                                isLoading = false,
                                errorMessage = it.message ?: "Something went wrong"
                            )
                        )
                    }
                    dispatch(ShowDetailAction.Error(it.message ?: "Something went wrong"))
                }
            }
        }
    }

    private fun updateWatchlist(action: UpdateWatchlist) {
        updateWatchlistInteractor.execute(action.params) {
            onError {
                coroutineScope.launch {
                    uiEffects.emit(
                        ShowDetailEffect.WatchlistError(it.message ?: "Something went wrong")
                    )
                }
            }
        }
    }

    private fun fetchSeason() {
        with(state) {
            seasonsInteractor.execute(showId) {
                onStart {
                    coroutineScope.launch {
                        emit(
                            value.copy(
                                isLoading = true,
                            )
                        )
                    }
                }
                onNext {
                    coroutineScope.launch {
                        emit(
                            value.copy(
                                isLoading = false,
                                tvSeasonUiModels = it
                            )
                        )
                    }
                }
                onError {
                    coroutineScope.launch {
                        emit(
                            value.copy(
                                isLoading = false,
                                errorMessage = it.message ?: "Something went wrong"
                            )
                        )
                    }
                }
                onComplete {
                    if (state.value.episodeList.isEmpty()) {
                        val season = state.value.tvSeasonUiModels.first()
                        coroutineScope.launch {
                            dispatch(
                                ShowDetailAction.LoadEpisodes(
                                    EpisodeQuery(
                                        tvShowId = showId,
                                        seasonId = season.seasonId,
                                        seasonNumber = season.seasonNumber
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun fetchEpisodes(query: EpisodeQuery) {
        with(state) {
            episodeInteractor.execute(query) {
                onStart {
                    coroutineScope.launch {
                        emit(
                            value.copy(
                                isLoading = true,
                            )
                        )
                    }
                }
                onNext {
                    coroutineScope.launch {
                        emit(
                            value.copy(
                                isLoading = false,
                                episodeList = it
                            )
                        )
                    }
                }
                onError {
                    coroutineScope.launch {
                        emit(
                            value.copy(
                                isLoading = false,
                                errorMessage = it.message ?: "Something went wrong"
                            )
                        )
                    }
                }
            }
        }
    }
}
