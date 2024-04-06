package com.thomaskioko.tvmaniac.presentation.trailers

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.data.trailers.implementation.TrailerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

typealias TrailersPresenterFactory =
  (
    ComponentContext,
    id: Long,
  ) -> TrailersPresenter

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
    coroutineScope.launch {
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
        coroutineScope.launch { loadTrailerInfo() }
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
    repository.observeTrailersStoreResponse(traktShowId).collectLatest { result ->
      when (result) {
        is Either.Left -> {
          _state.update { TrailerError(result.left.errorMessage) }
        }
        is Either.Right -> {
          _state.update {
            TrailersContent(
              selectedVideoKey = result.right.toTrailerList().firstOrNull()?.key,
              trailersList = result.right.toTrailerList(),
            )
          }
        }
      }
    }
  }
}
