package com.thomaskioko.tvmaniac.seasons

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomaskioko.tvmaniac.details.api.interactor.ObserveShowInteractor
import com.thomaskioko.tvmaniac.seasonepisodes.api.ObserveSeasonWithEpisodesInteractor
import com.thomaskioko.tvmaniac.seasonepisodes.api.SeasonsAction
import com.thomaskioko.tvmaniac.seasonepisodes.api.SeasonsAction.Error
import com.thomaskioko.tvmaniac.seasonepisodes.api.SeasonsAction.LoadSeasons
import com.thomaskioko.tvmaniac.seasonepisodes.api.SeasonsAction.LoadShowDetails
import com.thomaskioko.tvmaniac.seasonepisodes.api.SeasonsEffect
import com.thomaskioko.tvmaniac.seasonepisodes.api.SeasonsViewState
import com.thomaskioko.tvmaniac.shared.core.CoroutineScopeOwner
import com.thomaskioko.tvmaniac.shared.core.store.Store
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeasonsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val observeSeasonsInteractor: ObserveSeasonWithEpisodesInteractor,
    private val observeShow: ObserveShowInteractor,
) : Store<SeasonsViewState, SeasonsAction, SeasonsEffect>, CoroutineScopeOwner, ViewModel() {

    private val showId: Long = savedStateHandle.get("showId")!!
    private val state = MutableStateFlow(SeasonsViewState.Empty)
    private val uiEffects = MutableSharedFlow<SeasonsEffect>(extraBufferCapacity = 100)

    init {
        dispatch(LoadShowDetails)
        dispatch(LoadSeasons)
    }

    override val coroutineScope: CoroutineScope
        get() = viewModelScope

    override fun observeState(): StateFlow<SeasonsViewState> = state

    override fun observeSideEffect(): Flow<SeasonsEffect> = uiEffects

    override fun dispatch(action: SeasonsAction) {
        when (action) {
            LoadSeasons -> fetchSeason()
            LoadShowDetails -> loadShowDetails()
            is Error -> {
                coroutineScope.launch {
                    uiEffects.emit(SeasonsEffect.Error(action.message))
                }
            }
        }
    }

    private fun fetchSeason() {
        with(state) {
            observeSeasonsInteractor.execute(showId) {
                onStart {
                    coroutineScope.launch {
                        emit(value.copy(isLoading = true))
                    }
                }
                onNext {
                    coroutineScope.launch {
                        emit(
                            value.copy(
                                isLoading = it.isNullOrEmpty(),
                                seasonsWithEpisodes = it
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
                    dispatch(Error(it.message ?: "Something went wrong"))
                }
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
                }
            }
        }
    }
}
