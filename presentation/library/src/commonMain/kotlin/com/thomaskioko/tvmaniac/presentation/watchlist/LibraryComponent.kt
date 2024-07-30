package com.thomaskioko.tvmaniac.presentation.watchlist

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.shows.api.LibraryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

typealias LibraryComponentFactory =
  (
    ComponentContext,
    navigateToShowDetails: (showDetails: Long) -> Unit,
  ) -> LibraryComponent

@Inject
class LibraryComponent(
  @Assisted componentContext: ComponentContext,
  @Assisted private val navigateToShowDetails: (id: Long) -> Unit,
  private val repository: LibraryRepository,
  private val coroutineScope: CoroutineScope = componentContext.coroutineScope()
) : ComponentContext by componentContext {

  private val _state = MutableStateFlow<LibraryState>(LoadingShows)
  val state: StateFlow<LibraryState> = _state.asStateFlow()

  init {
    observeLibraryData()
  }

  fun dispatch(action: LibraryAction) {
    when (action) {
      is ReloadLibrary -> observeLibraryData()
      is LibraryShowClicked -> navigateToShowDetails(action.id)
    }
  }

  private fun observeLibraryData() {
    coroutineScope.launch {
      repository.observeLibrary().collectLatest { result ->
        result.fold(
          { failure -> _state.update { ErrorLoadingShows(failure.errorMessage) } },
          { success -> _state.update { LibraryContent(list = success.entityToLibraryShowList()) } },
        )
      }
    }
  }
}
