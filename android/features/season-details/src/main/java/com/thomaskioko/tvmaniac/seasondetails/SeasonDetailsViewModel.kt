package com.thomaskioko.tvmaniac.seasondetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomaskioko.tvmaniac.core.util.CoroutineScopeOwner
import com.thomaskioko.tvmaniac.seasondetails.api.Loading
import com.thomaskioko.tvmaniac.seasondetails.api.ObserveSeasonEpisodesInteractor
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonsAction
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonsEffect
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonsLoaded
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonsViewState
import com.thomaskioko.tvmaniac.seasondetails.api.UpdateSeasonEpisodesInteractor
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
class SeasonDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val updateSeasonEpisodesInteractor: UpdateSeasonEpisodesInteractor,
    private val observeSeasonEpisodesInteractor: ObserveSeasonEpisodesInteractor,
) : Store<SeasonsViewState, SeasonsAction, SeasonsEffect>, CoroutineScopeOwner, ViewModel() {

    private val showId: Int = savedStateHandle["showId"]!!

    override val state: MutableStateFlow<SeasonsViewState> = MutableStateFlow(Loading)

    private val uiEffects = MutableSharedFlow<SeasonsEffect>(extraBufferCapacity = 100)

    init {
        dispatch(SeasonsAction.LoadSeasons)

        observeSeasonEpisodes()
    }

    override val coroutineScope: CoroutineScope
        get() = viewModelScope

    override fun observeState(): StateFlow<SeasonsViewState> = state

    override fun observeSideEffect(): Flow<SeasonsEffect> = uiEffects

    override fun dispatch(action: SeasonsAction) {
        when (action) {
            SeasonsAction.LoadSeasons -> updateSeasonEpisodesInteractor.execute(showId) {}
            is SeasonsAction.Error -> {
                coroutineScope.launch {
                    uiEffects.emit(SeasonsEffect.Error(action.message))
                }
            }
        }
    }

    private fun observeSeasonEpisodes() {
        with(state) {
            observeSeasonEpisodesInteractor.execute(showId) {
                onStart {
                    coroutineScope.launch { emit(Loading) }
                }
                onNext {
                    coroutineScope.launch {
                        emit(SeasonsLoaded(result = it))
                    }
                }
                onError {
                    dispatch(SeasonsAction.Error(it.message ?: "Something went wrong"))
                }
            }
        }
    }
}
