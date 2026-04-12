package com.thomaskioko.tvmaniac.presenter.trailers

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.data.trailers.implementation.TrailerRepository
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@AssistedInject
public class TrailersPresenter(
    componentContext: ComponentContext,
    @Assisted private val traktShowId: Long,
    private val repository: TrailerRepository,
) {

    private val coroutineScope = componentContext.coroutineScope()
    private val _state = MutableStateFlow<TrailersState>(LoadingTrailers)

    init {
        coroutineScope.launch { observeTrailerInfo() }
    }

    public val state: StateFlow<TrailersState> = _state.asStateFlow()

    public val stateValue: Value<TrailersState> = state.asValue(coroutineScope)

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

    @AssistedFactory
    public fun interface Factory {
        public fun create(traktShowId: Long): TrailersPresenter
    }
}
