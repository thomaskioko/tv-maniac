package com.thomaskioko.tvmaniac.presentation.trailers

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.thomaskioko.tvmaniac.data.trailers.implementation.TrailerRepository
import com.thomaskioko.tvmaniac.util.model.Either
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

class TrailerScreenModel @Inject constructor(
    @Assisted private val traktShowId: Long,
    private val repository: TrailerRepository,
) : ScreenModel {

    private val _state = MutableStateFlow<TrailersState>(LoadingTrailers)
    val state = _state.asStateFlow()

    init {
        screenModelScope.launch {
            loadTrailerInfo()
            observeTrailerInfo()
        }
    }

    fun dispatch(action: TrailersAction) {
        when (action) {
            is VideoPlayerError -> {
                _state.value = TrailerError(action.errorMessage)
            }

            is TrailerSelected -> {
                _state.value = TrailersContent(selectedVideoKey = action.trailerKey)
            }

            ReloadTrailers -> {
                screenModelScope.launch {
                    loadTrailerInfo()
                }
            }
        }
    }

    private suspend fun loadTrailerInfo() {
        _state.value = LoadingTrailers
        val result = repository.fetchTrailersByShowId(traktShowId)
        _state.update {
            TrailersContent(
                selectedVideoKey = result.toTrailerList().firstOrNull()?.key,
                trailersList = result.toTrailerList(),
            )
        }
    }

    private suspend fun observeTrailerInfo() {
        repository.observeTrailersStoreResponse(traktShowId)
            .collectLatest { result ->
                when (result) {
                    is Either.Left -> {
                        _state.update { TrailerError(result.error.errorMessage) }
                    }
                    is Either.Right -> {
                        _state.update {
                            TrailersContent(
                                selectedVideoKey = result.data.toTrailerList().firstOrNull()?.key,
                                trailersList = result.data.toTrailerList(),
                            )
                        }
                    }
                }
            }
    }
}
