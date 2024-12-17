package com.thomaskioko.tvmaniac.presentation.watchlist

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.db.LibraryShows
import com.thomaskioko.tvmaniac.core.db.SearchWatchlist
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.shows.api.LibraryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class LibraryPresenterFactory(
  val create: (
    componentContext: ComponentContext,
    navigateToShowDetails: (showDetails: Long) -> Unit,
  ) -> LibraryPresenter,
)

@Inject
class LibraryPresenter(
  @Assisted componentContext: ComponentContext,
  @Assisted private val navigateToShowDetails: (id: Long) -> Unit,
  private val repository: LibraryRepository,
  private val coroutineScope: CoroutineScope = componentContext.coroutineScope(),
) : ComponentContext by componentContext {

  private val queryFlow = MutableStateFlow("")
  private val _state = MutableStateFlow<LibraryState>(LoadingShows)
  val state: StateFlow<LibraryState> = _state.asStateFlow()

  init {
    coroutineScope.launch {
      combine(
        queryFlow,
        repository.observeLibrary()
      ) { query, libraryResult ->
        query to libraryResult
      }.collect { (query, result) ->
        handleLibraryResult(query, result)
      }
    }
  }

  fun dispatch(action: LibraryAction) {
    when (action) {
      is ReloadLibrary -> refreshLibrary()
      is LibraryShowClicked -> navigateToShowDetails(action.id)
      is LibraryQueryChanged -> updateQuery(action.query)
      is ClearLibraryQuery -> clearQuery()
      ChangeListStyleClicked -> {
        //TODO:: Update settings with change style
      }
    }
  }

  private fun refreshLibrary() {
    coroutineScope.launch {
      observeLibraryData()
    }
  }

  private fun updateQuery(query: String) {
    coroutineScope.launch {
      queryFlow.emit(query)
      if (query.isNotBlank()) {
        searchLibrary(query)
      }
    }
  }

  private fun clearQuery() {
    coroutineScope.launch {
      queryFlow.emit("")
      observeLibraryData()
    }
  }

  private suspend fun searchLibrary(query: String) {
    repository.searchWatchlistByQuery(query)
      .catch { handleError(it.message) }
      .collect { result ->
        handleSearchResult(query, result)
      }
  }

  private suspend fun observeLibraryData() {
    repository.observeLibrary()
      .onStart { _state.update { LoadingShows } }
    .catch { handleError(it.message) }
    .collect { result ->
        handleLibraryResult(queryFlow.replayCache.lastOrNull() ?: "", result)
      }
  }

  private fun handleSearchResult(query: String, result: Either<Failure, List<SearchWatchlist>>) {
    result.fold(
      onFailure = { handleError(it.errorMessage, query) },
      onSuccess = { shows ->
        val list = shows.entityToLibraryShowList()

        _state.update { currentState ->
            (currentState as? LibraryContent)?.copy(
              query = query,
              isSearchActive = true,
              list = list
            ) ?: LibraryContent(
              list = list,
              query = query,
              isSearchActive = true
            )
        }
      }
    )
  }

  private fun handleLibraryResult(query: String, result: Either<Failure, List<LibraryShows>>) {
    result.fold(
      onFailure = { handleError(it.errorMessage, query) },
      onSuccess = { shows ->
        _state.update { currentState ->
          when (currentState) {
            is LibraryContent -> currentState.copy(
              list = shows.entityToLibraryShowList(),
              query = query,
              isSearchActive = query.isNotBlank()
            )
            else -> LibraryContent(
              list = shows.entityToLibraryShowList(),
              query = query,
              isSearchActive = query.isNotBlank()
            )
          }
        }
      }
    )
  }

  private fun handleError(error: String?, query: String = "") {
    _state.value = EmptyWatchlist(
      message = error ?: "Unknown error occurred",
      query = query,
      isSearchActive = query.isNotBlank()
    )
  }
}
