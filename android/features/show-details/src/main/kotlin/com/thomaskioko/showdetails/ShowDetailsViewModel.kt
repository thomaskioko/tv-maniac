package com.thomaskioko.showdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomaskioko.tvmaniac.core.util.CoroutineScopeOwner
import com.thomaskioko.tvmaniac.details.api.interactor.ObserveShowInteractor
import com.thomaskioko.tvmaniac.details.api.interactor.UpdateFollowingInteractor
import com.thomaskioko.tvmaniac.details.api.presentation.ShowDetailAction
import com.thomaskioko.tvmaniac.details.api.presentation.ShowDetailAction.UpdateFavorite
import com.thomaskioko.tvmaniac.details.api.presentation.ShowDetailEffect
import com.thomaskioko.tvmaniac.details.api.presentation.ShowDetailEffect.ShowDetailsError
import com.thomaskioko.tvmaniac.details.api.presentation.ShowDetailViewState
import com.thomaskioko.tvmaniac.genre.api.GetGenresInteractor
import com.thomaskioko.tvmaniac.lastairepisodes.api.ObserveAirEpisodesInteractor
import com.thomaskioko.tvmaniac.seasons.api.interactor.ObserveSeasonsInteractor
import com.thomaskioko.tvmaniac.shared.core.ui.Store
import com.thomaskioko.tvmaniac.similar.api.ObserveSimilarShowsInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShowDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val observeShow: ObserveShowInteractor,
    private val observeSimilarShows: ObserveSimilarShowsInteractor,
    private val observeSeasonsInteractor: ObserveSeasonsInteractor,
    private val genresInteractor: GetGenresInteractor,
    private val observeAirEpisodesInteractor: ObserveAirEpisodesInteractor,
    private val updateFollowingInteractor: UpdateFollowingInteractor
) : Store<ShowDetailViewState, ShowDetailAction, ShowDetailEffect>, CoroutineScopeOwner,
    ViewModel() {

    override val coroutineScope: CoroutineScope
        get() = viewModelScope

    private val showId: Long = savedStateHandle.get("tvShowId")!!

    private val state = MutableStateFlow(ShowDetailViewState.Empty)

    private val uiEffects = MutableSharedFlow<ShowDetailEffect>(extraBufferCapacity = 100)

    init {
        dispatch(ShowDetailAction.LoadShowDetails(showId))
        dispatch(ShowDetailAction.LoadSeasons(showId))
        dispatch(ShowDetailAction.LoadGenres)
        dispatch(ShowDetailAction.LoadEpisodes(showId))
        dispatch(ShowDetailAction.LoadSimilarShows)
    }

    override fun observeState(): StateFlow<ShowDetailViewState> = state

    override fun observeSideEffect(): Flow<ShowDetailEffect> = uiEffects

    override fun dispatch(action: ShowDetailAction) {
        when (action) {
            ShowDetailAction.LoadGenres -> fetchGenres()
            ShowDetailAction.LoadSimilarShows -> fetchSimilarShows()
            is ShowDetailAction.LoadSeasons -> fetchSeason()
            is ShowDetailAction.LoadShowDetails -> loadShowDetails()
            is UpdateFavorite -> updateWatchlist(action)
            is ShowDetailAction.Error -> {
                coroutineScope.launch {
                    uiEffects.emit(ShowDetailsError(action.message))
                }
            }
            is ShowDetailAction.LoadEpisodes -> fetchEpisodes()
            is ShowDetailAction.BookmarkEpisode -> {
                // TODO Update episode watchlist
            }
        }
    }

    private fun loadShowDetails() {
        with(state) {
            observeShow.execute(showId) {
                onStart {
                    coroutineScope.launch { emit(value.copy(isLoading = true)) }
                }
                onNext {
                    coroutineScope.launch {
                        emit(
                            value.copy(
                                isLoading = false,
                                tvShow = it
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

    private fun updateWatchlist(action: UpdateFavorite) {
        updateFollowingInteractor.execute(action.params) {
            onComplete { dispatch(ShowDetailAction.LoadShowDetails(showId)) }
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
            observeSeasonsInteractor.execute(showId) {
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
            }
        }
    }

    private fun fetchEpisodes() {
        with(state) {
            observeAirEpisodesInteractor.execute(showId) {
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
                                lastAirEpList = it
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

    private fun fetchSimilarShows() {
        with(state) {
            observeSimilarShows.execute(showId) {
                onStart {
                    coroutineScope.launch { emit(value.copy(isLoading = false)) }
                }
                onNext {
                    coroutineScope.launch {
                        emit(
                            value.copy(
                                isLoading = false,
                                similarShowList = it
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
}
