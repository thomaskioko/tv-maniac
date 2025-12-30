package com.thomaskioko.tvmaniac.presenter.trailers

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
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
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@ContributesBinding(ActivityScope::class, TrailersPresenter::class)
public class DefaultTrailersPresenter(
    @Assisted componentContext: ComponentContext,
    @Assisted private val traktShowId: Long,
    private val repository: TrailerRepository,
) : TrailersPresenter, ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()
    private val _state = MutableStateFlow<TrailersState>(LoadingTrailers)

    init {
        coroutineScope.launch { observeTrailerInfo() }
    }

    override val state: StateFlow<TrailersState> = _state.asStateFlow()

    override fun dispatch(action: TrailersAction) {
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

@Inject
@SingleIn(ActivityScope::class)
@ContributesBinding(ActivityScope::class, TrailersPresenter.Factory::class)
public class DefaultTrailersPresenterFactory(
    private val presenter: (
        componentContext: ComponentContext,
        traktShowId: Long,
    ) -> TrailersPresenter,
) : TrailersPresenter.Factory {
    override fun invoke(
        componentContext: ComponentContext,
        traktShowId: Long,
    ): TrailersPresenter = presenter(componentContext, traktShowId)
}
