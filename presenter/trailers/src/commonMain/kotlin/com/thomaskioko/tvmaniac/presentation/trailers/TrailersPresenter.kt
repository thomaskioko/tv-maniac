package com.thomaskioko.tvmaniac.presentation.trailers

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.data.trailers.implementation.TrailerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class TrailersPresenterFactory(
    val create: (
        componentContext: ComponentContext,
        id: Long,
    ) -> TrailersPresenter,
)

class TrailersPresenter
@Inject
constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val traktShowId: Long,
    private val repository: TrailerRepository,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()
    private val _state = MutableStateFlow<TrailersState>(LoadingTrailers)
    val state: StateFlow<TrailersState> = _state.asStateFlow()

    init {
        coroutineScope.launch { observeTrailerInfo() }
    }

    fun dispatch(action: TrailersAction) {
        coroutineScope.launch {
            when (action) {
                is VideoPlayerError -> _state.update { TrailerError(action.errorMessage) }
                is TrailerSelected ->
                    _state.update { TrailersContent(selectedVideoKey = action.trailerKey) }
                ReloadTrailers -> observeTrailerInfo()
            }
        }
    }

    private suspend fun observeTrailerInfo() {
        repository.observeTrailers(traktShowId)
            .collectLatest { result ->
                _state.update {
                    TrailersContent(
                        selectedVideoKey = result.toTrailerList().firstOrNull()?.key,
                        trailersList = result.toTrailerList(),
                    )
                }
            }
    }
}
