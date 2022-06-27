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
import com.thomaskioko.tvmaniac.shared.core.ui.Store
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
    private val updateFollowingInteractor: UpdateFollowingInteractor
) : Store<ShowDetailViewState, ShowDetailAction, ShowDetailEffect>, CoroutineScopeOwner,
    ViewModel() {

    private val showId: Long = savedStateHandle["tvShowId"]!!

    override val coroutineScope: CoroutineScope
        get() = viewModelScope

    override val state: MutableStateFlow<ShowDetailViewState> = MutableStateFlow(ShowDetailViewState.Empty)

    private val uiEffects = MutableSharedFlow<ShowDetailEffect>(extraBufferCapacity = 100)

    init {
        dispatch(ShowDetailAction.LoadShowDetails(showId))
    }

    override fun observeState(): StateFlow<ShowDetailViewState> = state

    override fun observeSideEffect(): Flow<ShowDetailEffect> = uiEffects

    override fun dispatch(action: ShowDetailAction) {
        when (action) {
            is ShowDetailAction.LoadShowDetails -> loadShowDetails()
            is UpdateFavorite -> updateWatchlist(action)
            is ShowDetailAction.Error -> {
                coroutineScope.launch {
                    uiEffects.emit(ShowDetailsError(action.message))
                }
            }
      /*      is ShowDetailAction.ShowDetailsLoaded -> {
                coroutineScope.launch {
                    state.emit(ShowDetailsLoadedState( action.result))
                }
            }*/
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
                                tvShow = it.tvShow,
                                similarShowList = it.similarShowList,
                                tvSeasonUiModels = it.tvSeasonUiModels,
                                genreUIList = it.genreUIList,
                                lastAirEpList = it.lastAirEpList
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
}
