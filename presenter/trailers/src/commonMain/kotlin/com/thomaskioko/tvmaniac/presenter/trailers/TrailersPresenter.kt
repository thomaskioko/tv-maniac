package com.thomaskioko.tvmaniac.presenter.trailers

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.data.trailers.implementation.TrailerRepository
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Inject
class TrailersPresenter(
    @Assisted componentContext: ComponentContext,
    @Assisted("trailerId") private val trailerId: Long,
    private val repository: TrailerRepository,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()
    private val _state = MutableStateFlow<TrailersState>(LoadingTrailers)

    init {
        coroutineScope.launch { observeTrailerInfo() }
    }

    public val state: StateFlow<TrailersState> = _state.asStateFlow()

    public fun dispatch(action: TrailersAction) {
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
        repository.observeTrailers(trailerId)
            .collectLatest { result ->
                _state.update {
                    TrailersContent(
                        selectedVideoKey = result.toTrailerList().firstOrNull()?.key,
                        trailersList = result.toTrailerList(),
                    )
                }
            }
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            @Assisted componentContext: ComponentContext,
            @Assisted("trailerId") traktShowId: Long,
        ): TrailersPresenter
    }
}
